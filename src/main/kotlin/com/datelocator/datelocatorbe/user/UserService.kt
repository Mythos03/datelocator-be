package com.datelocator.datelocatorbe.user

import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
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
}
