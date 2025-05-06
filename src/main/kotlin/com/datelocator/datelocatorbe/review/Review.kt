package com.datelocator.datelocatorbe.review

import com.datelocator.datelocatorbe.user.models.User
import com.datelocator.datelocatorbe.venue.models.Venue
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "reviews")
data class Review(
    @Id
    val id: UUID = UUID.randomUUID(),

    val rating: Double,
    val reviewText: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    val venue: Venue,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
)