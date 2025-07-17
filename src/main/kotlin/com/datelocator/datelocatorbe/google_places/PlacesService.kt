package com.datelocator.datelocatorbe.google_places

import com.google.maps.TextSearchRequest
import com.google.maps.model.PlacesSearchResponse
import org.springframework.stereotype.Service
import com.datelocator.datelocatorbe.config.GoogleMapsConfig
import com.google.maps.GeoApiContext

@Service
class PlacesService(private val geoApiContext: GeoApiContext) {
    fun findPlaces(query: String): PlacesSearchResponse {
        val request = TextSearchRequest(geoApiContext)
        return request.query(query).await()
    }
}