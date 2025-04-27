package com.datelocator.datelocatorbe.user

import com.datelocator.datelocatorbe.preference.Preference
import com.datelocator.datelocatorbe.preference.PreferenceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val preferenceRepository: PreferenceRepository
) {

    fun getUserById(uid: String): UserResponseDto? {
        val user: User = userRepository.findById(uid).orElse(null) ?: return null
        return UserMapper.toResponseDto(user)
    }

    fun createUser(user: User): User {
        return userRepository.save(user)
    }

    fun findByUsername(username: String): UserResponseDto? {
        val user: User = userRepository.findByUsername(username) ?: return null
        return UserMapper.toResponseDto(user)
    }

    fun getAllUsers(): List<UserResponseDto> {
        return userRepository.findAll().map { user: User ->
            UserMapper.toResponseDto(user)
        }
    }

    fun updateUserPreferences(uid: String, preferenceIds: Set<UUID>): UserResponseDto? {
        val user: User = userRepository.findById(uid).orElse(null) ?: return null
        val preferenceEntities: Set<Preference> = preferenceRepository.findAllById(preferenceIds).toSet()

        user.preferences.clear()
        user.preferences.addAll(preferenceEntities)

        val savedUser: User = userRepository.save(user)
        return UserMapper.toResponseDto(savedUser)
    }
}