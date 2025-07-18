package com.datelocator.datelocatorbe.review

import com.datelocator.datelocatorbe.review.models.Review
import com.datelocator.datelocatorbe.review.models.ReviewResponseDto

object ReviewMapper {
    fun toResponseDto(review: Review): ReviewResponseDto {
        return ReviewResponseDto(
            id = review.id,
            rating = review.rating,
            reviewText = review.reviewText,
            userId = review.user.firebaseUid,
            username = review.user.username
        )
    }
}