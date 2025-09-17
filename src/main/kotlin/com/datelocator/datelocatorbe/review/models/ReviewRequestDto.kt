package com.datelocator.datelocatorbe.review.models

import java.util.UUID

data class ReviewRequestDto(
    val rating: Double,
    val reviewText: String?,
    val venueId: UUID,
    val firebaseUid: String,
    val preferenceIds: MutableSet<UUID>? = mutableSetOf()
)
