package com.datelocator.datelocatorbe.preference

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PreferenceRepository : JpaRepository<Preference, UUID> {

}