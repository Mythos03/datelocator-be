package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.venue.models.RecommendedVenueRequestDto
import com.datelocator.datelocatorbe.venue.models.UpdateVenuePreferencesDto
import com.datelocator.datelocatorbe.venue.models.VenueRequestDto
import com.datelocator.datelocatorbe.venue.models.VenueResponseDto
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/venues")
class VenueController(
    private val venueService: VenueService,
    private val venueMapper: VenueMapper
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
                .status(HttpStatus.INTERNAL_SERVER_ERROR) //TODO: fix error messages
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

    @PostMapping(path = ["/recommended"])
    fun recommendedVenuesForUser(@RequestBody recommendedVenueRequestDto: RecommendedVenueRequestDto): ResponseEntity<List<VenueResponseDto>> {
        return ResponseEntity.ok(venueService.recommendedVenuesForUser(recommendedVenueRequestDto))
    }

    @GetMapping("/search")
    fun searchVenues(
        @RequestParam name: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): Page<VenueResponseDto> {
        logger.info("Searching venues with name: $name, page: $page, size: $size")
        val venues = venueService.searchVenuesByName(name, page, size)
        return venues.map { venue ->
            venueMapper.toResponseDto(venue)
        }
    }

    /**
     * Search for nearby venues using hybrid local DB + Google Places approach
     *
     * Query Params:
     * - lat: Latitude of search center (required)
     * - lng: Longitude of search center (required)
     * - radius: Search radius in meters (default: 5000)
     * - type: Optional Google Places type filter (e.g., "restaurant", "cafe")
     * - keycloakId: Optional user ID for attribution
     */
    @GetMapping("/nearby")
    fun searchNearbyVenues(
        @RequestParam lat: Double,
        @RequestParam lng: Double,
        @RequestParam(defaultValue = "5000") radius: Int,
        @RequestParam(required = false) type: String?,
        @RequestParam(required = false) keycloakId: String?
    ): ResponseEntity<List<VenueResponseDto>> {
        return try {
            logger.info("Searching for nearby venues at ($lat, $lng) with radius $radius")

            val venues = venueService.searchNearbyVenues(lat, lng, radius, type, keycloakId)
            logger.info("Returning ${venues.size} nearby venues")

            ResponseEntity.ok(venues)
        } catch (e: Exception) {
            logger.error("Failed to search nearby venues", e)
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(emptyList())
        }
    }

    /**
     * Get detailed venue information
     * Fetches from Google Places API if details not already cached
     *
     * Path variable 'id' can be either:
     * - Internal UUID
     * - Google Place ID
     */
    @GetMapping("/details/{id}")
    fun getVenueDetails(@PathVariable id: String): ResponseEntity<Any> {
        return try {
            logger.info("Fetching venue details for ID: $id")

            val venue = venueService.getVenueDetails(id)
                ?: return ResponseEntity.notFound().build()

            logger.info("Successfully retrieved venue details for: ${venue.name}")
            ResponseEntity.ok(venue)
        } catch (e: Exception) {
            logger.error("Failed to fetch venue details for ID: $id", e)
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to e.message))
        }
    }

}
