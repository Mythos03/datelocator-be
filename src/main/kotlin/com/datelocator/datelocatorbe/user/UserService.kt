package com.datelocator.datelocatorbe.user

import com.datelocator.datelocatorbe.image.models.EntityType
import com.datelocator.datelocatorbe.image.models.Image
import com.datelocator.datelocatorbe.preference.PreferenceRepository
import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.user.models.CreatePartialUserDto
import com.datelocator.datelocatorbe.user.models.UpdateProfilePictureDto
import com.datelocator.datelocatorbe.user.models.UpdateUserRequestDto
import com.datelocator.datelocatorbe.user.models.User
import com.datelocator.datelocatorbe.user.models.UserRequestDto
import com.datelocator.datelocatorbe.user.models.UserResponseDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val preferenceRepository: PreferenceRepository,
    private val userMapper: UserMapper
) {

    fun getUserById(uid: String): UserResponseDto? {
        val user: User = userRepository.findByKeycloakId(UUID.fromString(uid)) ?: return null
        return userMapper.toResponseDto(user)
    }
    fun getUserEntityById(uid: String): User? {
        return userRepository.findByKeycloakId(UUID.fromString(uid))
    }

    fun updatePartialUser(requestDto: UserRequestDto): UserResponseDto? {
        val existingUser = userRepository.findByKeycloakId(UUID.fromString(requestDto.keycloakId))
            ?: return null

        existingUser.username = requestDto.username
        existingUser.firstName = requestDto.firstName
        existingUser.lastName = requestDto.lastName
        existingUser.gender = requestDto.gender
        existingUser.age = requestDto.age
        existingUser.isComplete = true
        existingUser.image = requestDto.imageDownloadUrl?.let {
            Image(
                imageUrl = it,
                entityId = existingUser.id,
                entityType = EntityType.USER
            )
        }
        userRepository.save(existingUser)

        return userMapper.toResponseDto(existingUser)
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
        val user: User = userRepository.findByKeycloakId(UUID.fromString(uid)) ?: return null
        val preferenceEntities: Set<Preference> = preferenceRepository.findAllById(preferenceIds).toSet()

        user.preferences.clear()
        user.preferences.addAll(preferenceEntities)

        val savedUser: User = userRepository.save(user)
        return userMapper.toResponseDto(savedUser)
    }

    fun createPartialUser(requestDto: CreatePartialUserDto): User {
        return userRepository.save(userMapper.partialUserToUser(requestDto))
    }

    fun updateUser(keycloakId: String, updateUserRequestDto: UpdateUserRequestDto): UserResponseDto? {
        val existingUser = userRepository.findByKeycloakId(UUID.fromString(keycloakId))
            ?: return null

        existingUser.username = updateUserRequestDto.username
        existingUser.firstName = updateUserRequestDto.firstName
        existingUser.lastName = updateUserRequestDto.lastName
        existingUser.gender = updateUserRequestDto.gender
        existingUser.age = updateUserRequestDto.age

        userRepository.save(existingUser)

        return userMapper.toResponseDto(existingUser)
    }

    fun updateProfilePicture(keycloakId: String, updateProfilePictureDto: UpdateProfilePictureDto): UserResponseDto? {
        val existingUser = userRepository.findByKeycloakId(UUID.fromString(keycloakId)) ?: return null

        existingUser.image = updateProfilePictureDto.imageDownloadUrl?.let {
            Image(
                imageUrl = it,
                entityId = existingUser.id,
                entityType = EntityType.USER
            )
        }
        userRepository.save(existingUser)

        return userMapper.toResponseDto(existingUser)
    }
}