package com.datelocator.datelocatorbe.preference

import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.preference.models.PreferenceResponseDto
import org.springframework.stereotype.Component

@Component
object PreferenceMapper {
    fun toResponseDto(preference: Preference): PreferenceResponseDto {
        return PreferenceResponseDto(
            id = preference.id,
            name = preference.name
        )
    }
}