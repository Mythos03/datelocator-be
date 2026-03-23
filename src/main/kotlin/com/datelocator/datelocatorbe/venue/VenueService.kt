package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.preference.PreferenceService
import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.user.UserService
import com.datelocator.datelocatorbe.venue.models.*
import com.datelocator.datelocatorbe.votes.VenuePreferenceVote
import com.datelocator.datelocatorbe.votes.VenuePreferenceVoteRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class VenueService(
    private val venueRepository: VenueRepository,
    private val venueMapper: VenueMapper,
    private val preferenceService: PreferenceService,
    private val venuePreferenceVoteRepository: VenuePreferenceVoteRepository,
    private val userService: UserService,
    private val googlePlacesService: GooglePlacesService
) {
    private val logger = LoggerFactory.getLogger(VenueService::class.java)

    companion object {
        const val DEFAULT_VOTE_THRESHOLD = 1
        const val MIN_LOCAL_RESULTS_THRESHOLD = 5
    }

    fun createVenue(venueRequestDto: VenueRequestDto): Venue {
        try {
            logger.info("Starting venue creation with data: $venueRequestDto")


            val createdBy = venueRequestDto.keycloakId?.let { userId ->
                userService.getUserEntityByKeycloakId(userId)
            }

            val venue = venueMapper.toEntity(venueRequestDto, createdBy)
            logger.debug("Mapped venue entity: {}", venue)

            val savedVenue = venueRepository.save(venue)
            logger.debug("Saved venue with ID: {}", savedVenue.id)

            return savedVenue
        } catch (e: Exception) {
            logger.error("Failed to create venue", e)
            throw RuntimeException("Failed to create venue: ${e.message}", e)
        }
    }

    fun addPreferencesToVenue(venue: Venue, updateVenuePreferencesDto: UpdateVenuePreferencesDto) {
        try {
            logger.info("Adding preferences to venue: ${venue.id}")
            updateVenuePreferencesDto.preferenceIds
                .forEach { preferenceId ->
                    preferenceService.getPreferenceById(preferenceId)?.let { preference ->
                        val existingVote = venuePreferenceVoteRepository
                            .findByVenueIdAndPreferenceId(venue.id, preferenceId)

                        if (existingVote == null) {
                            val user = userService.getUserEntityByKeycloakId(updateVenuePreferencesDto.keycloakId)
                                ?: throw IllegalArgumentException("User not found")
                            val vote = VenuePreferenceVote(
                                venue = venue,
                                preference = preference,
                                user = user
                            )
                            venuePreferenceVoteRepository.save(vote)
                        } else {
                            existingVote.voteCount++
                            venuePreferenceVoteRepository.save(existingVote)
                        }
                    }
                }
        } catch (e: Exception) {
            logger.error("Failed to add preferences to venue", e)
            throw RuntimeException("Failed to add preferences: ${e.message}", e)
        }
    }

    fun getValidatedPreferences(venue: Venue, voteThreshold: Int = DEFAULT_VOTE_THRESHOLD): Set<Preference> {
        return venuePreferenceVoteRepository
            .findByVenueId(venue.id)
            .filter { it.voteCount >= voteThreshold }
            .map { it.preference }
            .toSet()
    }

    fun getVenueById(venueId: UUID): Venue? {
        return venueRepository.findById(venueId).orElse(null)
    }

    fun getVenueByGoogleId(googleId: String): VenueResponseDto? {
        val venue = venueRepository.findVenueByGooglePlacesId(googleId) ?: return null
        getValidatedPreferences(venue)
        return venueMapper.toResponseDto(venue)
    }

    fun venueExistsByGoogleId(googleId: String): Boolean {
        logger.info("Checking if venue exists with Google Places ID: $googleId")
        return venueRepository.existsByGooglePlacesId(googleId)
    }

    fun recommendedVenuesForUser(recommendedVenueRequestDto: RecommendedVenueRequestDto): List<VenueResponseDto> {
        val preferenceIds = preferenceService.returnPreferenceIdsByKeycloakId(recommendedVenueRequestDto.keycloakId)

        val venues = venueRepository.findRecommendedVenuesByProximityAndPreferences(recommendedVenueRequestDto.lat, recommendedVenueRequestDto.lng, preferenceIds, recommendedVenueRequestDto.minRating, recommendedVenueRequestDto.limit, recommendedVenueRequestDto.offset)

        return venues.map { venue ->
            getValidatedPreferences(venue)
            venueMapper.toResponseDto(venue)
        }
    }

    fun searchVenuesByName(name: String, page: Int, size: Int): Page<Venue> {
        val pageable = PageRequest.of(page, size)
        return venueRepository.findByNameContainingIgnoreCase(name, pageable)
    }

    /**
     * Search for venues using a hybrid approach:
     * 1. First check local database for venues within radius
     * 2. If insufficient results, query Google Places API
     * 3. Store new venues from Google Places for future queries
     * 4. Return merged results
     *
     * @param lat Latitude of search center
     * @param lng Longitude of search center
     * @param radiusMeters Search radius in meters
     * @param type Optional Google Places type filter
     * @param keycloakId Optional user ID for attribution
     * @return List of VenueResponseDto with combined local + Google results
     */
    fun searchNearbyVenues(
        lat: Double,
        lng: Double,
        radiusMeters: Int,
        type: String? = null,
        keycloakId: String? = null
    ): List<VenueResponseDto> {
        logger.info("Searching for nearby venues at ($lat, $lng) with radius $radiusMeters meters")

        // Convert meters to kilometers for Haversine formula
        val radiusKm = radiusMeters / 1000.0

        // Step 1: Query local database
        val localVenues = venueRepository.findVenuesWithinRadius(lat, lng, radiusKm)
        logger.info("Found ${localVenues.size} venues in local database")

        // Step 2: If we have sufficient local results, return them
        if (localVenues.size >= MIN_LOCAL_RESULTS_THRESHOLD) {
            logger.info("Sufficient local results found, returning local venues only")
            return localVenues.map { venue ->
                getValidatedPreferences(venue)
                venueMapper.toResponseDto(venue)
            }
        }

        // Step 3: Query Google Places API if configured
        val googlePlaces = if (googlePlacesService.isConfigured()) {
            logger.info("Insufficient local results, querying Google Places API")
            googlePlacesService.searchNearbyPlaces(lat, lng, radiusMeters, type)
        } else {
            logger.warn("Google Places API not configured, cannot fetch additional venues")
            emptyList()
        }

        // Step 4: Store new venues from Google Places
        val user = keycloakId?.let { userService.getUserEntityByKeycloakId(it) }
        val newVenues = googlePlaces
            .filter { !venueRepository.existsByGooglePlacesId(it.placeId) }
            .map { googlePlace ->
                try {
                    val venue = venueMapper.fromGooglePlaceDto(googlePlace, user)
                    val savedVenue = venueRepository.save(venue)
                    logger.debug("Saved new venue from Google Places: ${savedVenue.name}")
                    savedVenue
                } catch (e: Exception) {
                    logger.error("Failed to save venue from Google Places: ${googlePlace.name}", e)
                    null
                }
            }
            .filterNotNull()

        logger.info("Saved ${newVenues.size} new venues from Google Places API")

        // Step 5: Merge and return results
        val allVenues = localVenues + newVenues
        return allVenues.map { venue ->
            getValidatedPreferences(venue)
            venueMapper.toResponseDto(venue)
        }
    }

    /**
     * Get venue details, fetching from Google Places API if needed
     *
     * This method implements cost-efficient caching:
     * 1. Check if detailed info already exists in DB
     * 2. If not, call Google Places API with field masking
     * 3. Enrich and persist the details
     * 4. Always return openingHours as null per requirements
     *
     * @param venueId Internal venue UUID or Google Place ID
     * @return VenueResponseDto with detailed information
     */
    fun getVenueDetails(venueId: String): VenueResponseDto? {
        logger.info("Fetching venue details for ID: $venueId")

        // Try to parse as UUID first, otherwise treat as Google Place ID
        val venue = try {
            val uuid = UUID.fromString(venueId)
            venueRepository.findById(uuid).orElse(null)
        } catch (e: IllegalArgumentException) {
            venueRepository.findVenueByGooglePlacesId(venueId)
        }

        if (venue == null) {
            logger.warn("Venue not found for ID: $venueId")
            return null
        }

        // Check if we need to enrich with Google Places data
        val needsEnrichment = venue.formattedAddress == null && googlePlacesService.isConfigured()

        val enrichedVenue = if (needsEnrichment) {
            logger.info("Venue lacks details, fetching from Google Places API")
            try {
                val details = googlePlacesService.getPlaceDetails(venue.googlePlacesId)
                if (details != null) {
                    val updated = venueMapper.enrichFromGooglePlaceDetails(venue, details)
                    venueRepository.save(updated)
                    logger.info("Successfully enriched venue with Google Places details")
                    updated
                } else {
                    logger.warn("Failed to fetch details from Google Places API")
                    venue
                }
            } catch (e: Exception) {
                logger.error("Error enriching venue with Google Places data", e)
                venue
            }
        } else {
            venue
        }

        getValidatedPreferences(enrichedVenue)
        return venueMapper.toResponseDto(enrichedVenue)
    }
}
