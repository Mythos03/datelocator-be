package com.datelocator.datelocatorbe.venue.models

data class OpeningHoursRequestDto(
    val openNow: Boolean,
    val weekdayText: List<String>?
)
