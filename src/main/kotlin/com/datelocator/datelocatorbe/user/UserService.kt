package com.datelocator.datelocatorbe.user

import com.datelocator.datelocatorbe.preference.PreferenceRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val preferenceRepository: PreferenceRepository
) {

    fun getUserById(uid: String): UserResponseDto? {
        return userRepository.findById(uid).orElse(null)
            ?.let { UserMapper.toResponseDto(it) }
    }

    fun createUser(user: User): User {
        return userRepository.save(user)
    }

    fun findByUsername(username: String): UserResponseDto? {
        return userRepository.findByUsername(username)
            ?.let { UserMapper.toResponseDto(it) }
    }

    fun getAllUsers(): List<UserResponseDto> {
        return userRepository.findAll()
            .map { UserMapper.toResponseDto(it) }
    }

    fun updateUserPreferences(uid: String, preferenceIds: Set<UUID>): UserResponseDto? {
        val user = userRepository.findById(uid).orElse(null) ?: return null
        val preferenceEntities = preferenceRepository.findAllById(preferenceIds).toSet()

        val updatedUser = user.copy(preferences = preferenceEntities)
        return UserMapper.toResponseDto(userRepository.save(updatedUser))
    }
}
