package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.preference.PreferenceService
import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.review.ReviewRepository
import com.datelocator.datelocatorbe.user.UserService
import com.datelocator.datelocatorbe.venue.models.UpdateVenuePreferencesDto
import com.datelocator.datelocatorbe.venue.models.Venue
import com.datelocator.datelocatorbe.venue.models.VenueRequestDto
import com.datelocator.datelocatorbe.votes.VenuePreferenceVote
import com.datelocator.datelocatorbe.votes.VenuePreferenceVoteRepository
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
    companion object {
        const val DEFAULT_VOTE_THRESHOLD = 1
    }


    fun createVenue(venueRequestDto: VenueRequestDto): Venue {
        val venue = venueMapper.toEntity(venueRequestDto)

        // Handle review association if needed
        venueRequestDto.reviewId?.let { reviewId ->
            val review = reviewRepository.findById(reviewId).orElse(null)
            review?.let { venue.reviews.add(it) }
        }

        return venueRepository.save(venue)
    }

    fun addPreferencesToVenue(venue: Venue, updateVenuePreferencesDto: UpdateVenuePreferencesDto) {
        updateVenuePreferencesDto.preferenceIds
            .mapNotNull { preferenceIdString ->
                runCatching {
                    UUID.fromString(preferenceIdString)
                }.getOrNull()
            }
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


}