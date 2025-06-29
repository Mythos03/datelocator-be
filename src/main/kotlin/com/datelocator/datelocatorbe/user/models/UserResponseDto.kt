package com.datelocator.datelocatorbe.user.models

data class UserResponseDto (
    val firebaseUid: String,
    val username: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val gender: Genders?,
    val age: Int? = null,
    val preferences: Set<String> = emptySet(),
)