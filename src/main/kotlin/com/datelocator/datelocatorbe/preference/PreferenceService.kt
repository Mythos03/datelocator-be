package com.datelocator.datelocatorbe.preference

import com.datelocator.datelocatorbe.user.User
import com.datelocator.datelocatorbe.user.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PreferenceService(
    private val preferenceRepository: PreferenceRepository,
    private val userRepository: UserRepository,
) {

    fun getAllPreferences(): List<Preference> {
        return preferenceRepository.findAll()
    }

    fun createPreference(preferenceRequestDto: PreferenceRequestDto): Preference {
        val preference = Preference(
            id = preferenceRequestDto.id ?: UUID.randomUUID(),
            name = preferenceRequestDto.name,
            users = emptySet()
        )
        return preferenceRepository.save(preference)
    }
}