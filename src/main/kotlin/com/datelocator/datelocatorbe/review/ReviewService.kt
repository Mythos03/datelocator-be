package com.datelocator.datelocatorbe.review

import com.datelocator.datelocatorbe.preference.PreferenceRepository
import com.datelocator.datelocatorbe.review.models.Review
import com.datelocator.datelocatorbe.review.models.ReviewRequestDto
import com.datelocator.datelocatorbe.review.models.ReviewResponseDto
import com.datelocator.datelocatorbe.user.UserService
import com.datelocator.datelocatorbe.venue.VenueService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val userService: UserService,
    private val venueService: VenueService,
    private val preferenceRepository: PreferenceRepository,
) {
    fun getReviewById(id: UUID): ReviewResponseDto? {
        val review = reviewRepository.findById(id).orElse(null) ?: return null
        return ReviewMapper.toResponseDto(review)
    }

    fun createReview(reviewRequestDto: ReviewRequestDto): ReviewResponseDto {
        val user = userService.getUserEntityById(reviewRequestDto.userId)
            ?: throw IllegalArgumentException("User not found")

        val venue = venueService.getVenueById(reviewRequestDto.venueId)
            ?: throw IllegalArgumentException("Venue not found")

        val preferences  = preferenceRepository.findAllById(reviewRequestDto.preferenceIds ?: emptySet())
        if (preferences.isEmpty()) {
            throw IllegalArgumentException("Preferences not found")
        }

        val review = Review(
            rating = reviewRequestDto.rating,
            reviewText = reviewRequestDto.reviewText,
            user = user,
            venue = venue,
            preferences = preferences.toMutableSet()
        )

        val savedReview = reviewRepository.save(review)
        return ReviewMapper.toResponseDto(savedReview)
    }
}