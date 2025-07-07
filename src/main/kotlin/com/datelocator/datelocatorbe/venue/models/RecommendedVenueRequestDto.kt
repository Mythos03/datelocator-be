package com.datelocator.datelocatorbe.venue.models

data class RecommendedVenueRequestDto(
    val userId: String,
    val lat: Double,
    val lng: Double,
)
