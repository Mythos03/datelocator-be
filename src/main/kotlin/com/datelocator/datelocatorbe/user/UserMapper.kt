package com.datelocator.datelocatorbe.user

object UserMapper {
    fun toResponseDto(user: User): UserResponseDto {
        return UserResponseDto(
            firebaseUid = user.firebaseUid,
            username = user.username ?: "",
            firstName = user.firstName,
            lastName = user.lastName,
            gender = user.gender,
            age = user.age,
            preferences = user.preferences.map { it.name }.toSet()
        )
    }
}