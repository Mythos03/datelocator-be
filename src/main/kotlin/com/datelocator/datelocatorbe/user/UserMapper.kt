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
            username = user.username ?: "",
            gender = user.gender,
            age = user.age,
            preferences = user.preferences.map { preferenceMapper.toResponseDto(it) }.toSet(),
            isComplete = user.isComplete
        )
    }
}
