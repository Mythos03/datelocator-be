package com.datelocator.datelocatorbe.venue.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.UUID

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