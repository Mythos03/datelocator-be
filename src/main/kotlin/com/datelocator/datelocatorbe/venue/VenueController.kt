package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.venue.models.UpdateVenuePreferencesDto
import com.datelocator.datelocatorbe.venue.models.Venue
import com.datelocator.datelocatorbe.venue.models.VenueRequestDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/venues")
class VenueController(
    private val venueService: VenueService
) {
    private val logger = LoggerFactory.getLogger(VenueController::class.java)

    @PostMapping
    fun createVenue(@RequestBody venueRequestDto: VenueRequestDto): ResponseEntity<Any> {
        return try {
            logger.info("Received venue creation request")
            logger.debug("Request data: {}", venueRequestDto)

            val venue = venueService.createVenue(venueRequestDto)
            logger.info("Venue created successfully with ID: ${venue.id}")

            ResponseEntity.ok(venue)
        } catch (e: Exception) {
            logger.error("Failed to create venue", e)
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to e.message))
        }
    }

    @PutMapping("/{venueId}/preferences")
    fun addPreferencesToVenue(
        @RequestBody updateVenuePreferencesDto: UpdateVenuePreferencesDto,
        @PathVariable venueId: UUID
    ): ResponseEntity<Any> {
        return try {
            logger.info("Updating preferences for venue: $venueId")
            logger.debug("Update data: $updateVenuePreferencesDto")

            val venue = venueService.getVenueById(venueId)
                ?: return ResponseEntity.notFound().build()

            venueService.addPreferencesToVenue(venue, updateVenuePreferencesDto)
            logger.info("Successfully updated venue preferences")

            ResponseEntity.ok(venue)
        } catch (e: Exception) {
            logger.error("Failed to update venue preferences", e)
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to e.message))
        }
    }
    @GetMapping("/{uid}")
    fun getVenueByGoogleId(@PathVariable uid: String): ResponseEntity<Any> {
        return try {
            logger.info("Fetching venue with Google ID: $uid")

            val venue = venueService.getVenueByGoogleId(uid)
                ?: return ResponseEntity.notFound().build()

            logger.info("Successfully retrieved venue with Google ID: $uid")

            ResponseEntity.ok(venue)
        } catch (e: Exception) {
            logger.error("Failed to retrieve venue with Google ID: $uid", e)
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to e.message))
        }
    }

    @RequestMapping(path = ["/{googlePlacesId}"], method = [RequestMethod.HEAD])
    fun checkVenueExists(@PathVariable googlePlacesId: String): ResponseEntity<Void> {
        return try {
            logger.info("Checking if venue exists with Google Places ID: $googlePlacesId")

            val venueExists = venueService.venueExistsByGoogleId(googlePlacesId)
            logger.debug("Venue exists result for Google Places ID $googlePlacesId: $venueExists")

            if (venueExists) {
                logger.info("Venue with Google Places ID: $googlePlacesId exists")
                ResponseEntity.ok().build()
            } else {
                logger.info("Venue with Google Places ID: $googlePlacesId not found")
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            logger.error("Error checking if venue exists with Google Places ID: $googlePlacesId", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}
