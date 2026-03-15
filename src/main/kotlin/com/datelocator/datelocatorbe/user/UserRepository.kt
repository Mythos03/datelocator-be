package com.datelocator.datelocatorbe.user

import com.datelocator.datelocatorbe.user.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByUsername(username: String): User?

    fun findByKeycloakId(keycloakId: UUID): User?
}
