package com.datelocator.datelocatorbe.user

import com.datelocator.datelocatorbe.user.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByUsername(username: String): User?

    fun findByfirebaseUid(firebaseUid: String): User?
}
