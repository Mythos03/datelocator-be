package com.datelocator.datelocatorbe.user.models

data class UpdateUserRequestDto(
    val username: String?,
    val gender: Genders?,
    val age: Int?,
)
