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
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(VenueService::class.java)

    companion object {
        const val DEFAULT_VOTE_THRESHOLD = 1
    }

    fun createVenue(venueRequestDto: VenueRequestDto): Venue {
        try {
            logger.info("Starting venue creation with data: $venueRequestDto")


            val createdBy = venueRequestDto.firebaseUid?.let { userId ->
                userService.getUserEntityById(userId)
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
                            val user = userService.getUserEntityById(updateVenuePreferencesDto.firebaseUid)
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
        val preferenceIds = preferenceService.returnPreferenceIdsByUserId(recommendedVenueRequestDto.firebaseUid)

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
}
