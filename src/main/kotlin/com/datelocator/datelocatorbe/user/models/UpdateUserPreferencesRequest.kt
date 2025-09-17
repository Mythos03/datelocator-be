package com.datelocator.datelocatorbe.user.models

import java.util.*

data class UpdateUserPreferencesRequest(
    val preferences: Set<UUID> = emptySet()
)
