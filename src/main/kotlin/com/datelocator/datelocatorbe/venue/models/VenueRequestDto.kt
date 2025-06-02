package com.datelocator.datelocatorbe.venue.models

import java.util.UUID

data class VenueRequestDto(
    val googlePlacesId: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val openingHoursRequestDto: OpeningHoursRequestDto? = null,
    val userId: String? = null
)
