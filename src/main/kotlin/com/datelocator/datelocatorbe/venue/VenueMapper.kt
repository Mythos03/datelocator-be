package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.review.ReviewService
import com.datelocator.datelocatorbe.venue.models.Venue
import com.datelocator.datelocatorbe.venue.models.VenueRequestDto
import org.springframework.stereotype.Component

@Component
class VenueMapper{
    fun toEntity(venueRequestDto: VenueRequestDto): Venue {
        return Venue(
            googlePlacesId = venueRequestDto.googlePlacesId,
            name = venueRequestDto.name,
            lat = venueRequestDto.lat,
            lng = venueRequestDto.lng,
            openingHours = venueRequestDto.openingHours?.let { OpeningHoursMapper.toEntity(it) },
            reviews = HashSet(),
        )
    }
}