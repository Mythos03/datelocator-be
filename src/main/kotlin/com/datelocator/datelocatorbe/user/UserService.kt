package com.datelocator.datelocatorbe.user

import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.preference.PreferenceRepository
import com.datelocator.datelocatorbe.user.models.User
import com.datelocator.datelocatorbe.user.models.UserRequestDto
import com.datelocator.datelocatorbe.user.models.UserResponseDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val preferenceRepository: PreferenceRepository,
    private val userMapper: UserMapper
) {

    fun getUserById(uid: String): UserResponseDto? {
        val user: User = userRepository.findById(uid).orElse(null) ?: return null
        return userMapper.toResponseDto(user)
    }
    fun getUserEntityById(uid: String): User? {
        return userRepository.findById(uid).orElse(null)
    }

    fun createUser(user: UserRequestDto): User {
        return userRepository.save(userMapper.userRequestDtoToUser(user))
    }

    fun findByUsername(username: String): UserResponseDto? {
        val user: User = userRepository.findByUsername(username) ?: return null
        return userMapper.toResponseDto(user)
    }

    fun getAllUsers(): List<UserResponseDto> {
        return userRepository.findAll().map { user: User ->
            userMapper.toResponseDto(user)
        }
    }

    fun updateUserPreferences(uid: String, preferenceIds: Set<UUID>): UserResponseDto? {
        val user: User = userRepository.findById(uid).orElse(null) ?: return null
        val preferenceEntities: Set<Preference> = preferenceRepository.findAllById(preferenceIds).toSet()

        user.preferences.clear()
        user.preferences.addAll(preferenceEntities)

        val savedUser: User = userRepository.save(user)
        return userMapper.toResponseDto(savedUser)
    }
}