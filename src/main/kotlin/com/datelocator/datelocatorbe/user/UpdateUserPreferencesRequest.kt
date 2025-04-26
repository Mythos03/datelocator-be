package com.datelocator.datelocatorbe.user

import java.util.UUID

data class UpdateUserPreferencesRequest(
    val preferences: Set<UUID> = emptySet()
)
