package com.datelocator.datelocatorbe.preference

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/preferences")
class PreferenceController(
    private val preferenceService: PreferenceService
) {
    @GetMapping
    fun getAllPreferences(): ResponseEntity<List<PreferenceResponseDto>> {
        return ResponseEntity.ok(preferenceService.getAllPreferences())
    }

    @PostMapping
    fun createPreference(@RequestBody preferenceRequestDto: PreferenceRequestDto): ResponseEntity<Preference> {
        val preference = preferenceService.createPreference(preferenceRequestDto)
        return ResponseEntity.ok(preference)
    }
}