package com.datelocator.datelocatorbe.review.models

import java.util.*

data class ReviewRequestDto(
    val rating: Double,
    val reviewText: String?,
    val venueId: UUID,
    val keycloakId: String,
    val preferenceIds: MutableSet<UUID>? = mutableSetOf(),
    val imageUrls: MutableSet<String>? = mutableSetOf()
)
