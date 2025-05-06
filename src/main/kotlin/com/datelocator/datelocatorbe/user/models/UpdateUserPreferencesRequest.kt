package com.datelocator.datelocatorbe.user.models

import java.util.UUID

data class UpdateUserPreferencesRequest(
    val preferences: Set<UUID> = emptySet()
)
