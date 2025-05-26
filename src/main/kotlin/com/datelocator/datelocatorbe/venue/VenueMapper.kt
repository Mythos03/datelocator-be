package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.user.models.User
import com.datelocator.datelocatorbe.venue.models.OpeningHours
import com.datelocator.datelocatorbe.venue.models.OpeningHoursRequestDto
import com.datelocator.datelocatorbe.venue.models.Venue
import com.datelocator.datelocatorbe.venue.models.VenueRequestDto
import org.springframework.stereotype.Component

@Component
class VenueMapper {
    fun toEntity(venueRequestDto: VenueRequestDto, createdBy: User? = null): Venue {
        return Venue(
            googlePlacesId = venueRequestDto.googlePlacesId,
            name = venueRequestDto.name,
            lat = venueRequestDto.lat,
            lng = venueRequestDto.lng,
            openingHours = venueRequestDto.openingHours?.let { toOpeningHours(it) },
            createdBy = createdBy,
            reviews = mutableSetOf()
        )
    }

    private fun toOpeningHours(dto: OpeningHoursRequestDto): OpeningHours {
        return OpeningHours(
            openNow = dto.openNow,
            weekdayText = dto.weekdayText?.toList()?.toMutableList() ?: mutableListOf()
        )
    }
}
