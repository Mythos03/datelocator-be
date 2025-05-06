package com.datelocator.datelocatorbe.user

import com.datelocator.datelocatorbe.user.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findByUsername(username: String): User?
}
