package com.datelocator.datelocatorbe.preference

import com.datelocator.datelocatorbe.user.User
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "preferences")
data class Preference(
    @Id
    val id: UUID = UUID.randomUUID(),

    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
)