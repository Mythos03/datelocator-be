package com.datelocator.datelocatorbe.user

import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun getUserById(uid: String): User? {
        return userRepository.findById(uid).orElse(null)
    }

    fun createUser(user: User): User {
        return userRepository.save(user)
    }

    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }
}
