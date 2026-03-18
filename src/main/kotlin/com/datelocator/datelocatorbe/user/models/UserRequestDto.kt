package com.datelocator.datelocatorbe.user.models

import java.util.UUID

data class UserRequestDto(
        val gender: Genders,
        val age: Int,
        val preferences: Set<UUID>? = emptySet()
)
