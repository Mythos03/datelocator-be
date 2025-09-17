package com.datelocator.datelocatorbe.venue.models

data class VenueRequestDto(
    val googlePlacesId: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val openingHoursRequestDto: OpeningHoursRequestDto? = null,
    val firebaseUid: String? = null
)
