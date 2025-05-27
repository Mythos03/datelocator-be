package com.datelocator.datelocatorbe.venue.models

import com.datelocator.datelocatorbe.review.models.ReviewResponseDto
import java.util.UUID

data class VenueResponseDto(
    val id: UUID,
    val name: String,
    val lng: Double,
    val lat: Double,
    val openingHours: OpeningHours? = null,
    val preferences: MutableSet<String>? = mutableSetOf(),
    val reviews: MutableSet<ReviewResponseDto>? = mutableSetOf()
)
