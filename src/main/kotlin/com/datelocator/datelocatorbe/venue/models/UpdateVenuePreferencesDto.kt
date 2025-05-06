package com.datelocator.datelocatorbe.venue.models

data class UpdateVenuePreferencesDto(
    val preferenceIds: List<String>,
    val userId: String
)
