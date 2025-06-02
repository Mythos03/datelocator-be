package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.review.ReviewMapper
import com.datelocator.datelocatorbe.user.UserService
import com.datelocator.datelocatorbe.user.models.User
import com.datelocator.datelocatorbe.venue.models.Venue
import com.datelocator.datelocatorbe.venue.models.VenueRequestDto
import com.datelocator.datelocatorbe.venue.models.VenueResponseDto
import org.springframework.stereotype.Component

@Component
class VenueMapper(
    private val openingHoursMapper: OpeningHoursMapper,
    private val userService: UserService
){
    fun toEntity(venueRequestDto: VenueRequestDto, createdBy: User? = null): Venue {
        val user = venueRequestDto.userId?.let { userService.getUserEntityById(it) } ?: createdBy

        return Venue(
            googlePlacesId = venueRequestDto.googlePlacesId,
            name = venueRequestDto.name,
            lat = venueRequestDto.lat,
            lng = venueRequestDto.lng,
            openingHours = venueRequestDto.openingHoursRequestDto?.let { openingHoursMapper.toEntity(it) },
            createdBy = user,
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

}
