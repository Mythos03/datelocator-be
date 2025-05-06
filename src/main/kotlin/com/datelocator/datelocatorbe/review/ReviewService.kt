package com.datelocator.datelocatorbe.review

import com.datelocator.datelocatorbe.review.models.Review
import com.datelocator.datelocatorbe.review.models.ReviewRequestDto
import com.datelocator.datelocatorbe.user.UserService
import com.datelocator.datelocatorbe.venue.VenueService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val userService: UserService,
    private val venueService: VenueService
) {
    fun getReviewById(id: UUID): Review? {
        return reviewRepository.findById(id).orElse(null)
    }

    fun createReview(reviewRequestDto: ReviewRequestDto): Review {
        val user = userService.getUserEntityById(reviewRequestDto.userId)
            ?: throw IllegalArgumentException("User not found")

        val venue = venueService.getVenueById(reviewRequestDto.venueId)
            ?: throw IllegalArgumentException("Venue not found")

        val review = Review(
            rating = reviewRequestDto.rating,
            reviewText = reviewRequestDto.reviewText,
            user = user,
            venue = venue
        )

        return reviewRepository.save(review)
    }
}