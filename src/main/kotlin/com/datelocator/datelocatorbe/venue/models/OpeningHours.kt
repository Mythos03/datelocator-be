package com.datelocator.datelocatorbe.venue.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "opening_hours")
data class OpeningHours(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ElementCollection
    @CollectionTable(
        name = "weekday_texts",
        joinColumns = [JoinColumn(name = "opening_hours_id")],
    )
    @Column(name = "text")
    val weekdayText: MutableList<String> = mutableListOf(), // Ensure mutable list

    @JsonIgnore
    @OneToOne(mappedBy = "openingHours")
    val venue: Venue? = null,
)