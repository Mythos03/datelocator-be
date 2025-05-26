package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.venue.models.OpeningHours
import com.datelocator.datelocatorbe.venue.models.OpeningHoursRequestDto

object OpeningHoursMapper {
    fun toEntity(openingHoursRequestDto: OpeningHoursRequestDto): OpeningHours {
        return OpeningHours(
            weekdayText = (openingHoursRequestDto.weekdayText?.map { it } ?: emptyList()) as MutableList<String>?
        )
    }
}