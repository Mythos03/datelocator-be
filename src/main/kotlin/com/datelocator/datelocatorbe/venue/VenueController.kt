package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.venue.models.Venue
import com.datelocator.datelocatorbe.venue.models.VenueRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/venues")
class VenueController(
    private val venueService: VenueService
) {
    @PostMapping
    fun createVenue(@RequestBody venueRequestDto: VenueRequestDto): ResponseEntity<Venue> {
        return ResponseEntity.ok(venueService.createVenue(venueRequestDto))
    }
}