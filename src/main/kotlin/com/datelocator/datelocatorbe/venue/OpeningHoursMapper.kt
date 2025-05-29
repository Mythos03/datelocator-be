package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.venue.models.OpeningHours
import com.datelocator.datelocatorbe.venue.models.OpeningHoursRequestDto
import org.springframework.stereotype.Component

@Component
class OpeningHoursMapper {
    fun toEntity(openingHoursRequestDto: OpeningHoursRequestDto): OpeningHours? {
        // If weekdayText is empty, return null for openingHours
        if (openingHoursRequestDto.weekdayText.isEmpty()) {
            return null
        }

        return OpeningHours(
            weekdayText = openingHoursRequestDto.weekdayText.map { sanitizeText(it) }.toMutableList()
        )
    }

    private fun sanitizeText(text: String): String {
        return text
            .replace(Regex("[\\u00A0\\u202F\\u2007\\u2009\\u200A\\u3000\\uFEFF]"), " ") // replace all non-standard spaces
            .replace(Regex("\\s+"), " ") // normalize multiple spaces
            .trim()
    }

}
