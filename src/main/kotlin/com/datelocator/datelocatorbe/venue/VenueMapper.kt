package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.review.ReviewMapper
import com.datelocator.datelocatorbe.user.UserService
import com.datelocator.datelocatorbe.user.models.User
import com.datelocator.datelocatorbe.venue.models.*
import com.datelocator.datelocatorbe.votes.VenuePreferenceVoteService
import org.springframework.stereotype.Component

@Component
class VenueMapper(
    private val openingHoursMapper: OpeningHoursMapper,
    private val userService: UserService,
    private val venuePreferenceVoteService: VenuePreferenceVoteService,
){
    fun toEntity(venueRequestDto: VenueRequestDto, createdBy: User? = null): Venue {
        val user = venueRequestDto.keycloakId?.let { userService.getUserEntityByKeycloakId(it) } ?: createdBy

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

    /**
     * Convert GooglePlaceDto to Venue entity
     * Used when creating venues from Google Places API search results
     */
    fun fromGooglePlaceDto(googlePlaceDto: GooglePlaceDto, createdBy: User? = null): Venue {
        return Venue(
            googlePlacesId = googlePlaceDto.placeId,
            name = googlePlaceDto.name,
            lat = googlePlaceDto.lat,
            lng = googlePlaceDto.lng,
            types = googlePlaceDto.types?.toMutableList() ?: mutableListOf(),
            priceLevel = googlePlaceDto.priceLevel,
            googleRating = googlePlaceDto.rating,
            googleRatingsTotal = googlePlaceDto.userRatingsTotal,
            createdBy = createdBy,
            reviews = mutableSetOf()
        )
    }

    /**
     * Enrich existing Venue entity with Google Place Details
     * Used when fetching detailed information from Google Places API
     */
    fun enrichFromGooglePlaceDetails(venue: Venue, details: GooglePlaceDetailsDto): Venue {
        return venue.copy(
            formattedAddress = details.formattedAddress,
            formattedPhoneNumber = details.formattedPhoneNumber,
            website = details.website,
            types = details.types?.toMutableList() ?: venue.types,
            priceLevel = details.priceLevel,
            businessStatus = details.businessStatus,
            googleRating = details.rating,
            googleRatingsTotal = details.userRatingsTotal
        )
    }

    fun toResponseDto(venue: Venue): VenueResponseDto {
        return VenueResponseDto(
            id = venue.id,
            name = venue.name,
            lat = venue.lat,
            lng = venue.lng,
            openingHours = null,  // Always return null per requirements
            preferences = venuePreferenceVoteService.getPreferenceIdsByVenueId(venue.id),
            reviews = venue.reviews.map { ReviewMapper.toResponseDto(it) }.toMutableSet(),
            averageRating = venue.averageRating,
            reviewCount = venue.reviewCount,
        )
    }

}
