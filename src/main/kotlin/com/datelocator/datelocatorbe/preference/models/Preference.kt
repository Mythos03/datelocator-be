package com.datelocator.datelocatorbe.preference.models

import com.datelocator.datelocatorbe.user.models.User
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "preferences")
data class Preference(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String,

    @ManyToMany(mappedBy = "preferences")
    @Schema(hidden = true)
    val users: MutableSet<User> = mutableSetOf(),
)