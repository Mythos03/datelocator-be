package com.datelocator.datelocatorbe.preference

import com.datelocator.datelocatorbe.preference.models.Preference
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface PreferenceRepository : JpaRepository<Preference, UUID> {
    @Query("SELECT p FROM Preference p JOIN p.users u WHERE u.id = :firebaseUid")
    fun findPreferencesByFirebaseUid(@Param("firebaseUid") firebaseUid: String): List<Preference>
}
