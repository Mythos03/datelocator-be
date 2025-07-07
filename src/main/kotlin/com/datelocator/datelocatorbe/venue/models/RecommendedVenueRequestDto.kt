package com.datelocator.datelocatorbe.venue.models

data class RecommendedVenueRequestDto(
    val userId: String,
    val lat: Double,
    val lng: Double,
    val minRating: Double,
    val limit: Int,
    val offset: Int,
)
