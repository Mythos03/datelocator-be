package com.datelocator.datelocatorbe.user.models

import java.util.UUID

data class UserRequestDto(
        val keycloakId: String,
        val username: String,
        val firstName: String,
        val lastName: String,
        val gender: Genders,
        val age: Int,
        val preferences: Set<UUID> = emptySet()
)
