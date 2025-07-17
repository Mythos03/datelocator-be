package com.datelocator.datelocatorbe.google_places

import com.google.maps.model.PlacesSearchResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/places")
class PlacesController(private val placesService: PlacesService) {

    @GetMapping("/search")
    fun searchPlaces(@RequestParam query: String): PlacesSearchResponse {
        return placesService.findPlaces(query)
    }
}