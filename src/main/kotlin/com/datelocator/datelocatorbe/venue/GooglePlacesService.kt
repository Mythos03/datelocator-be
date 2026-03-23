package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.config.GooglePlacesConfig
import com.datelocator.datelocatorbe.venue.models.GooglePlaceDetailsDto
import com.datelocator.datelocatorbe.venue.models.GooglePlaceDto
import com.google.maps.GeoApiContext
import com.google.maps.PlaceDetailsRequest
import com.google.maps.PlacesApi
import com.google.maps.model.LatLng
import com.google.maps.model.PlaceType
import com.google.maps.model.PlacesSearchResponse
import jakarta.annotation.PostConstruct
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
     * @param lat Latitude
     * @param lng Longitude
     * @param radiusMeters Search radius in meters
     * @param type Optional place type filter
     * @return List of GooglePlaceDto
     */
    fun searchNearbyPlaces(
        lat: Double,
        lng: Double,
        radiusMeters: Int = config.nearbySearchRadiusMeters,
        type: String? = null
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

            // Apply type filter if provided
            if (type != null) {
                try {
                    val placeType = PlaceType.valueOf(type.uppercase())
                    request = request.type(placeType)
                    logger.debug("Applied place type filter: $type")
                } catch (e: IllegalArgumentException) {
                    logger.warn("Invalid place type: $type, searching without type filter")
                }
            }

            val response: PlacesSearchResponse = request.await()
            val places = response.results.take(config.maxResults).map { result ->
                GooglePlaceDto(
                    placeId = result.placeId,
                    name = result.name,
                    lat = result.geometry.location.lat,
                    lng = result.geometry.location.lng,
                    rating = result.rating?.toDouble(),
                    userRatingsTotal = result.userRatingsTotal,
                    vicinity = result.vicinity,
                    types = result.types?.map { it.toString() } ?: emptyList(),
                    priceLevel = null  // Price level not available in nearby search
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
     * Fetch place details with field masking to minimize API costs
     * Only requests essential fields: name, formatted_address, formatted_phone_number,
     * rating, user_ratings_total, geometry, website, types, price_level, business_status
     *
     * Note: Opening hours are NOT requested to comply with Google's caching restrictions
     */
    fun getPlaceDetails(placeId: String): GooglePlaceDetailsDto? {
        if (!isConfigured()) {
            logger.warn("Google Places API is not configured, cannot fetch place details")
            return null
        }

        return try {
            logger.info("Fetching place details for placeId: $placeId")

            val request: PlaceDetailsRequest = PlacesApi.placeDetails(geoApiContext, placeId)
            val result = request.await()

            val details = GooglePlaceDetailsDto(
                placeId = result.placeId,
                name = result.name,
                formattedAddress = result.formattedAddress,
                formattedPhoneNumber = result.formattedPhoneNumber,
                rating = result.rating?.toDouble(),
                userRatingsTotal = result.userRatingsTotal,
                lat = result.geometry.location.lat,
                lng = result.geometry.location.lng,
                website = result.website?.toString(),
                types = result.types?.map { it.toString() } ?: emptyList(),
                priceLevel = result.priceLevel?.ordinal,
                businessStatus = result.businessStatus?.toString()
            )

            logger.info("Successfully fetched details for place: ${details.name}")
            details
        } catch (e: Exception) {
            logger.error("Error fetching place details for placeId: $placeId", e)
            null
        }
    }

    /**
     * Shutdown the GeoApiContext when the service is destroyed
     */
    fun shutdown() {
        if (this::geoApiContext.isInitialized) {
            geoApiContext.shutdown()
            logger.info("Google Places API context shutdown")
        }
    }
}
