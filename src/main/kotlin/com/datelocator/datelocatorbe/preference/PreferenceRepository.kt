package com.datelocator.datelocatorbe.preference

import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.user.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface PreferenceRepository : JpaRepository<Preference, UUID> {
    @Query("SELECT p FROM Preference p JOIN p.users u WHERE u.firebaseUid = :userId")
    fun findPreferencesByUserId(@Param("userId") userId: String): List<Preference>
}
