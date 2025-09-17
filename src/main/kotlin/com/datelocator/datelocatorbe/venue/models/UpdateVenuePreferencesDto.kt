package com.datelocator.datelocatorbe.venue.models

import java.util.*

data class UpdateVenuePreferencesDto(
    val preferenceIds: Set<UUID>,
    val firebaseUid: String
)
