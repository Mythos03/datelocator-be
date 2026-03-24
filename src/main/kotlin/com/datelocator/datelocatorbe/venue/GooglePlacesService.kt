package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.config.GooglePlacesConfig
import com.datelocator.datelocatorbe.venue.models.GooglePlaceDto
import com.google.maps.GeoApiContext
import com.google.maps.PlacesApi
import com.google.maps.model.LatLng
import com.google.maps.model.PlaceType
import com.google.maps.model.PlacesSearchResponse
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class GooglePlacesService(
    private val config: GooglePlacesConfig
) {
    private val logger = LoggerFactory.getLogger(GooglePlacesService::class.java)
    private lateinit var geoApiContext: GeoApiContext

    @PostConstruct
    fun init() {
        if (config.key.isBlank()) {
            logger.warn("Google Places API key is not configured. Google Places integration will be disabled.")
        } else {
            geoApiContext = GeoApiContext.Builder()
                .apiKey(config.key)
                .build()
            logger.info("Google Places API initialized successfully")
        }
    }

    fun isConfigured(): Boolean {
        return config.key.isNotBlank()
    }

    /**
     * Search for nearby places using Google Places Nearby Search API
     * Returns only basic information: placeId, name, lat, lng
     *
     * @param lat Latitude
     * @param lng Longitude
     * @param radiusMeters Search radius in meters
     * @param types Optional comma-separated place type filters (e.g., "restaurant,cafe")
     * @return List of GooglePlaceDto with basic venue information
     */
    fun searchNearbyPlaces(
        lat: Double,
        lng: Double,
        radiusMeters: Int = config.nearbySearchRadiusMeters,
        types: String? = null
    ): List<GooglePlaceDto> {
        if (!isConfigured()) {
            logger.warn("Google Places API is not configured, returning empty results")
            return emptyList()
        }

        return try {
            logger.info("Searching for nearby places at ($lat, $lng) with radius $radiusMeters meters")

            val location = LatLng(lat, lng)
            var request = PlacesApi.nearbySearchQuery(geoApiContext, location)
                .radius(radiusMeters)

            // Apply type filters if provided (comma-separated)
            if (!types.isNullOrBlank()) {
                val typeList = types.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                typeList.forEach { typeStr ->
                    try {
                        val placeType = PlaceType.valueOf(typeStr.uppercase())
                        request = request.type(placeType)
                        logger.debug("Applied place type filter: $typeStr")
                    } catch (e: IllegalArgumentException) {
                        logger.warn("Invalid place type: $typeStr, skipping")
                    }
                }
            }

            val response: PlacesSearchResponse = request.await()
            val places = response.results.take(config.maxResults).map { result ->
                GooglePlaceDto(
                    placeId = result.placeId,
                    name = result.name,
                    lat = result.geometry.location.lat,
                    lng = result.geometry.location.lng
                )
            }

            logger.info("Found ${places.size} places from Google Places API")
            places
        } catch (e: Exception) {
            logger.error("Error searching for nearby places", e)
            // Return empty list on error to allow graceful degradation
            emptyList()
        }
    }

    /**
     * Shutdown the GeoApiContext when the service is destroyed
     */
    @PreDestroy
    fun shutdown() {
        if (this::geoApiContext.isInitialized) {
            geoApiContext.shutdown()
            logger.info("Google Places API context shutdown")
        }
    }
}
