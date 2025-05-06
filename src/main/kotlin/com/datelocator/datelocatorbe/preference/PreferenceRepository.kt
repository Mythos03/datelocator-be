package com.datelocator.datelocatorbe.preference

import com.datelocator.datelocatorbe.preference.models.Preference
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PreferenceRepository : JpaRepository<Preference, UUID> {

}