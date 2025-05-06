package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.venue.models.UpdateVenuePreferencesDto
import com.datelocator.datelocatorbe.venue.models.Venue
import com.datelocator.datelocatorbe.venue.models.VenueRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/venues")
class VenueController(
    private val venueService: VenueService
) {
    @PostMapping
    fun createVenue(@RequestBody venueRequestDto: VenueRequestDto): ResponseEntity<Venue> {
        return ResponseEntity.ok(venueService.createVenue(venueRequestDto))
    }

    @PutMapping("/{venueId}/preferences")
    fun addPreferencesToVenue(@RequestBody updateVenuePreferencesDto: UpdateVenuePreferencesDto, @PathVariable venueId: UUID): ResponseEntity<Venue> {
        val updatedVenue = venueService.getVenueById(venueId)
        if (updatedVenue == null) {
            return ResponseEntity.notFound().build()
        } else {
            venueService.addPreferencesToVenue(updatedVenue, updateVenuePreferencesDto)
            return ResponseEntity.ok(updatedVenue)
        }
    }
}