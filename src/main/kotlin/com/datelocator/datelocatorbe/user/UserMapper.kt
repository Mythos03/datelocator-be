package com.datelocator.datelocatorbe.user

import com.datelocator.datelocatorbe.user.models.User
import com.datelocator.datelocatorbe.user.models.UserRequestDto
import com.datelocator.datelocatorbe.user.models.UserResponseDto
import org.springframework.stereotype.Component

@Component
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

    fun userRequestDtoToUser(userRequestDto: UserRequestDto): User {
        return User(
            firebaseUid = userRequestDto.firebaseUid,
            username = userRequestDto.username,
            firstName = userRequestDto.firstName,
            lastName = userRequestDto.lastName,
            gender = userRequestDto.gender,
            age = userRequestDto.age,
        )
    }
}