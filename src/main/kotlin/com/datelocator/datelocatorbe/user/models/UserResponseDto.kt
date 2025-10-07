package com.datelocator.datelocatorbe.user.models

import com.datelocator.datelocatorbe.preference.models.PreferenceResponseDto

data class UserResponseDto (
    val firebaseUid: String,
    val username: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val gender: Genders?,
    val age: Int? = null,
    val preferences: Set<PreferenceResponseDto> = emptySet(),
    val imageUrl: String? = null,
)