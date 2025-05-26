package com.datelocator.datelocatorbe.venue.models

import java.util.UUID

data class UpdateVenuePreferencesDto(
    val preferenceIds: Set<UUID>,
    val userId: String
)
