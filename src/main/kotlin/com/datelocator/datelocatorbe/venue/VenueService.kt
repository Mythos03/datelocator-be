package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.preference.PreferenceService
import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.review.ReviewRepository
import com.datelocator.datelocatorbe.user.UserService
import com.datelocator.datelocatorbe.venue.models.UpdateVenuePreferencesDto
import com.datelocator.datelocatorbe.venue.models.Venue
import com.datelocator.datelocatorbe.venue.models.VenueRequestDto
import com.datelocator.datelocatorbe.venue.models.VenueResponseDto
import com.datelocator.datelocatorbe.votes.VenuePreferenceVote
import com.datelocator.datelocatorbe.votes.VenuePreferenceVoteRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class VenueService(
    private val venueRepository: VenueRepository,
    private val venueMapper: VenueMapper,
    private val preferenceService: PreferenceService,
    private val venuePreferenceVoteRepository: VenuePreferenceVoteRepository,
    private val userService: UserService,
    private val reviewRepository: ReviewRepository
) {
    private val logger = LoggerFactory.getLogger(VenueService::class.java)

    companion object {
        const val DEFAULT_VOTE_THRESHOLD = 1
    }

    fun createVenue(venueRequestDto: VenueRequestDto): Venue {
        try {
            logger.info("Starting venue creation with data: $venueRequestDto")


            val createdBy = venueRequestDto.userId?.let { userId ->
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
                            val user = userService.getUserEntityById(updateVenuePreferencesDto.userId)
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
        val validatedPreferences = getValidatedPreferences(venue)
        return venueMapper.toResponseDto(venue, validatedPreferences)
    }

    fun venueExistsByGoogleId(googleId: String): Boolean {
        logger.info("Checking if venue exists with Google Places ID: $googleId")
        return venueRepository.existsByGooglePlacesId(googleId)
    }
}
