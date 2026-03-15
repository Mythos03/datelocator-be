package com.datelocator.datelocatorbe.user.models

data class UserRequestDto(
    val keycloakId: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val gender: Genders,
    val age: Int,
    val imageDownloadUrl: String? = null,
)
