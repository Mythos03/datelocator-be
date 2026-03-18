package com.datelocator.datelocatorbe.user.models

import com.datelocator.datelocatorbe.preference.models.PreferenceResponseDto

data class UserResponseDto (
    val username: String,
    val gender: Genders?,
    val age: Int? = null,
    val preferences: Set<PreferenceResponseDto> = emptySet(),
)