package com.datelocator.datelocatorbe.preference

object PreferenceMapper {
    fun toResponseDto(preference: Preference): PreferenceResponseDto {
        return PreferenceResponseDto(
            id = preference.id,
            name = preference.name
        )
    }
}