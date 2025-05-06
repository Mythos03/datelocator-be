package com.datelocator.datelocatorbe.preference

import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.preference.models.PreferenceRequestDto
import com.datelocator.datelocatorbe.preference.models.PreferenceResponseDto
import org.springframework.stereotype.Service
import java.util.UUID

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

    fun getPreferenceById(id: UUID): Preference? {
        return preferenceRepository.findById(id).orElse(null)
    }
}