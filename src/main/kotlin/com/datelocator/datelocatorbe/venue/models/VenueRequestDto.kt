package com.datelocator.datelocatorbe.venue.models

import java.util.UUID

data class VenueRequestDto(
    val googlePlacesId: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val openingHours: OpeningHoursRequestDto? = null,
    val preferenceIds: MutableSet<UUID>? = mutableSetOf(), // Ensure mutable set
    val reviewId: UUID? = null,
    val userId: String? = null
)
