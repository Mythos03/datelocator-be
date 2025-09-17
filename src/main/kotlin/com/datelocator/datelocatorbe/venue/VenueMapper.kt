package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.review.ReviewMapper
import com.datelocator.datelocatorbe.user.UserService
import com.datelocator.datelocatorbe.user.models.User
import com.datelocator.datelocatorbe.venue.models.Venue
import com.datelocator.datelocatorbe.venue.models.VenueRequestDto
import com.datelocator.datelocatorbe.venue.models.VenueResponseDto
import com.datelocator.datelocatorbe.votes.VenuePreferenceVoteRepository
import com.datelocator.datelocatorbe.votes.VenuePreferenceVoteService
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class VenueMapper(
    private val openingHoursMapper: OpeningHoursMapper,
    private val userService: UserService,
    private val venuePreferenceVoteService: VenuePreferenceVoteService,
){
    fun toEntity(venueRequestDto: VenueRequestDto, createdBy: User? = null): Venue {
        val user = venueRequestDto.firebaseUid?.let { userService.getUserEntityById(it) } ?: createdBy

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

    fun toResponseDto(venue: Venue): VenueResponseDto {
        return VenueResponseDto(
            id = venue.id,
            name = venue.name,
            lat = venue.lat,
            lng = venue.lng,
            openingHours = venue.openingHours,
            preferences = venuePreferenceVoteService.getPreferenceIdsByVenueId(venue.id),
            reviews = venue.reviews.map { ReviewMapper.toResponseDto(it) }.toMutableSet(),
            averageRating = venue.averageRating,
            reviewCount = venue.reviewCount,
        )
    }

}
