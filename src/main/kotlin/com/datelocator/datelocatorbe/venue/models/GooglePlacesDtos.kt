package com.datelocator.datelocatorbe.venue.models

data class NearbySearchRequestDto(
    val lat: Double,
    val lng: Double,
    val radiusMeters: Int,
    val type: String? = null
)

data class GooglePlaceDto(
    val placeId: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val rating: Double? = null,
    val userRatingsTotal: Int? = null,
    val vicinity: String? = null,
    val types: List<String>? = null,
    val priceLevel: Int? = null
)

data class PlaceDetailsRequestDto(
    val placeId: String,
    val fields: List<String>? = null
)

data class GooglePlaceDetailsDto(
    val placeId: String,
    val name: String,
    val formattedAddress: String? = null,
    val formattedPhoneNumber: String? = null,
    val rating: Double? = null,
    val userRatingsTotal: Int? = null,
    val lat: Double,
    val lng: Double,
    val website: String? = null,
    val types: List<String>? = null,
    val priceLevel: Int? = null,
    val businessStatus: String? = null
)
