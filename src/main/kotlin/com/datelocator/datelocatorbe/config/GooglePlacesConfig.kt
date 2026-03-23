package com.datelocator.datelocatorbe.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "google.places.api")
data class GooglePlacesConfig(
    var key: String = "",
    var nearbySearchRadiusMeters: Int = 5000,
    var maxResults: Int = 20
)
