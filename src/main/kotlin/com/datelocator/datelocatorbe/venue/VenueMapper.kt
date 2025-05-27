package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.review.ReviewMapper
import com.datelocator.datelocatorbe.user.models.User
import com.datelocator.datelocatorbe.venue.models.OpeningHours
import com.datelocator.datelocatorbe.venue.models.OpeningHoursRequestDto
import com.datelocator.datelocatorbe.venue.models.Venue
import com.datelocator.datelocatorbe.venue.models.VenueRequestDto
import com.datelocator.datelocatorbe.venue.models.VenueResponseDto
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

    fun toResponseDto(venue: Venue, validatedPreferences: Set<Preference>): VenueResponseDto {
        return VenueResponseDto(
            id = venue.id,
            name = venue.name,
            lat = venue.lat,
            lng = venue.lng,
            openingHours = venue.openingHours,
            preferences = validatedPreferences.map { it.id.toString() }.toMutableSet(),
            reviews = venue.reviews.map { ReviewMapper.toResponseDto(it) }.toMutableSet()
        )
    }

    private fun toOpeningHours(dto: OpeningHoursRequestDto): OpeningHours {
        return OpeningHours(
            weekdayText = dto.weekdayText?.toList()?.toMutableList() ?: mutableListOf()
        )
    }
}