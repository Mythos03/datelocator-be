package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.venue.models.Venue
import com.datelocator.datelocatorbe.venue.models.VenueRequestDto
import org.springframework.stereotype.Service

@Service
class VenueService(
    private val venueRepository: VenueRepository,
) {
    fun getVenueById(id: String): VenueResponseDto? {
        val venue = venueRepository.findById(id).orElse(null) ?: return null
        return VenueMapper.toResponseDto(venue)
    }

    fun createVenue(venueRequestDto: VenueRequestDto): Venue {
        return venueRepository.save(venue)
    }
}