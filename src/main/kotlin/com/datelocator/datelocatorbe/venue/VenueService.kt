package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.venue.models.Venue
import com.datelocator.datelocatorbe.venue.models.VenueRequestDto
import org.springframework.stereotype.Service

@Service
class VenueService(
    private val venueRepository: VenueRepository,
    private val venueMapper: VenueMapper
) {
    fun createVenue(venueRequestDto: VenueRequestDto): Venue {
        return venueRepository.save(
            venueMapper.toEntity(venueRequestDto)
        )
    }
}