package com.datelocator.datelocatorbe.review

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository
) {
    fun getReviewById(id: UUID): Review? {
        return reviewRepository.findById(id).orElse(null)
    }

    fun createReview(review: Review): Review {
        return reviewRepository.save(review)
    }
}