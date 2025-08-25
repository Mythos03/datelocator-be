package com.datelocator.datelocatorbe.user.models

import java.util.UUID

data class UserRequestDto(
    val username: String,
    val firstName: String,
    val lastName: String,
    val gender: Genders,
    val preferenceIds: Set<UUID> = emptySet(),
    val age: Int,
)
