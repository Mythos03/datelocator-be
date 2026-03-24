package com.datelocator.datelocatorbe.venue.models

/**
 * DTO for nearby search request parameters
 */
data class NearbySearchRequestDto(
    val lat: Double,
    val lng: Double,
    val radiusMeters: Int,
    val type: String? = null
)

/**
 * Simplified DTO for Google Place from Nearby Search API
 * Contains only the fields we store in our database
 */
data class GooglePlaceDto(
    val placeId: String,
    val name: String,
    val lat: Double,
    val lng: Double
)
