package com.datelocator.datelocatorbe.preference.models

import com.datelocator.datelocatorbe.user.models.User
import com.datelocator.datelocatorbe.venue.models.Venue
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "preferences")
data class Preference(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String,

    @ManyToMany(mappedBy = "preferences")
    @Schema(hidden = true)
    val users: MutableSet<User> = mutableSetOf(),

    @ManyToMany(mappedBy = "preferences")
    @Schema(hidden = true)
    val venues: MutableSet<Venue> = mutableSetOf()
)