package com.datelocator.datelocatorbe.user.models

data class UpdateUserRequestDto(
    val username: String,
    val firstName: String,
    val lastName: String,
    val gender: Genders,
    val age: Int,
)
