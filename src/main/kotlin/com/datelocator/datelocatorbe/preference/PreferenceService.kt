package com.datelocator.datelocatorbe.preference

import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.math.max

@Service
class PreferenceService(
    private val preferenceRepository: PreferenceRepository,
) {

    fun getAllPreferences(): List<PreferenceResponseDto> {
        return preferenceRepository.findAll()
            .map { PreferenceMapper.toResponseDto(it) }
    }

    fun createPreference(preferenceRequestDto: PreferenceRequestDto): Preference {
        val preference = Preference(
            id = preferenceRequestDto.id ?: UUID.randomUUID(),
            name = preferenceRequestDto.name,
            users = mutableSetOf()
        )
        return preferenceRepository.save(preference)
    }
}