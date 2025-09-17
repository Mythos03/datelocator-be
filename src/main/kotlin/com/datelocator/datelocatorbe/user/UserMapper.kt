package com.datelocator.datelocatorbe.user

import com.datelocator.datelocatorbe.preference.PreferenceMapper
import com.datelocator.datelocatorbe.user.models.CreatePartialUserDto
import com.datelocator.datelocatorbe.user.models.User
import com.datelocator.datelocatorbe.user.models.UserRequestDto
import com.datelocator.datelocatorbe.user.models.UserResponseDto
import org.springframework.stereotype.Component

@Component
class UserMapper (
    private val preferenceMapper: PreferenceMapper
) {
    fun toResponseDto(user: User): UserResponseDto {
        return UserResponseDto(
            firebaseUid = user.firebaseUid,
            username = user.username ?: "",
            firstName = user.firstName,
            lastName = user.lastName,
            gender = user.gender,
            age = user.age,
            preferences = user.preferences.map { preferenceMapper.toResponseDto(it) }.toSet()
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

    fun partialUserToUser(createPartialUserDto: CreatePartialUserDto): User {
        return User(
            firebaseUid = createPartialUserDto.firebaseUid,
        )
    }
}
