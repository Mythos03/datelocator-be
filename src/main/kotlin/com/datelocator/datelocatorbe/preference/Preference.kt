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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_preferences",
        joinColumns = [JoinColumn(name = "preference_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    @Schema(hidden = true)
    val users: Set<User> = emptySet()
)