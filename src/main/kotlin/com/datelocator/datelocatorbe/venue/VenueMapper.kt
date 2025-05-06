package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.preference.PreferenceService
import com.datelocator.datelocatorbe.review.ReviewService
import com.datelocator.datelocatorbe.venue.models.Venue
import com.datelocator.datelocatorbe.venue.models.VenueRequestDto
import org.springframework.stereotype.Component

@Component
class VenueMapper(
    private val reviewService: ReviewService,
    private val preferenceService: PreferenceService
) {
    fun toEntity(venueRequestDto: VenueRequestDto): Venue {
        return Venue(
            googlePlacesId = venueRequestDto.googlePlacesId,
            name = venueRequestDto.name,
            lat = venueRequestDto.lat,
            lng = venueRequestDto.lng,
            openingHours = venueRequestDto.openingHours?.let { OpeningHoursMapper.toEntity(it) },
            reviews = mutableSetOf(),
            preferences = mutableSetOf()
        ).apply {
            venueRequestDto.reviewId?.let { reviewId ->
                reviewService.getReviewById(reviewId)?.let { reviews.add(it) }
            }
            venueRequestDto.preferenceIds?.forEach { preferenceId ->
                preferenceService.getPreferenceById(preferenceId)?.let { preferences.add(it) }
            }
        }
    }
}