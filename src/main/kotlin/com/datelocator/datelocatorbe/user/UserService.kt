package com.datelocator.datelocatorbe.user

import com.datelocator.datelocatorbe.image.models.EntityType
import com.datelocator.datelocatorbe.image.models.Image
import com.datelocator.datelocatorbe.preference.PreferenceRepository
import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.user.models.UpdateProfilePictureDto
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

    fun syncUser(keycloakId: String, username: String): UserResponseDto {
        val keycloakUuid = UUID.fromString(keycloakId)
        val user = userRepository.findByKeycloakId(keycloakUuid)
            ?.apply {
                if (this.username != username) {
                    this.username = username
                }
            }
            ?: User(
                keycloakId = keycloakUuid,
                username = username,
                isComplete = false
            )

        return userMapper.toResponseDto(userRepository.save(user))
    }

    fun getUserEntityByKeycloakId(uid: String): User? {
        return userRepository.findByKeycloakId(UUID.fromString(uid))
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

    fun updateUser(keycloakId: UUID, userRequestDto: UserRequestDto): UserResponseDto? {
        val existingUser = userRepository.findByKeycloakId(keycloakId)
            ?: return null

        existingUser.gender = userRequestDto.gender
        existingUser.age = userRequestDto.age
        existingUser.preferences = userRequestDto.preferences?.let { preferenceIds ->
            preferenceRepository.findAllById(preferenceIds).toMutableSet()
        } ?: mutableSetOf()
        existingUser.isComplete = true

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
