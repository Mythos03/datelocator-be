package com.datelocator.datelocatorbe.venue.models

import com.datelocator.datelocatorbe.review.models.ReviewResponseDto
import java.util.*

data class VenueResponseDto(
    val id: UUID,
    val name: String,
    val lng: Double,
    val lat: Double,
    val openingHours: OpeningHours? = null,
    val preferences: Set<UUID>? = emptySet(),
    val reviews: MutableSet<ReviewResponseDto>? = mutableSetOf(),
    val averageRating: Double,
    val reviewCount: Int,
)
