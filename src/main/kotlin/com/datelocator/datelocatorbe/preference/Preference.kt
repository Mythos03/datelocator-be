package com.datelocator.datelocatorbe.preference

import com.datelocator.datelocatorbe.user.User
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
    val users: MutableSet<User> = mutableSetOf()
)