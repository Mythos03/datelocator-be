package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.venue.models.OpeningHours
import com.datelocator.datelocatorbe.venue.models.OpeningHoursRequestDto

object OpeningHoursMapper {
    fun toEntity(openingHoursRequestDto: OpeningHoursRequestDto): OpeningHours {
        return OpeningHours(
            weekdayText = openingHoursRequestDto.weekdayText
                ?.map { it.replace(Regex("[\\u202F\\u2009]"), " ") } // Replace NNBSP and THSP with regular spaces
                ?.toMutableList()
        )
    }
}