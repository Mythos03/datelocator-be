package com.datelocator.datelocatorbe.review.models

import java.util.UUID

data class ReviewResponseDto(
    val id: UUID,
    val rating: Double,
    val reviewText: String?,
    val venueId: UUID,
    val venueName: String,
    val userId: String,
    val username: String?
)