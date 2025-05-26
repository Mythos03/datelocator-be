package com.datelocator.datelocatorbe.venue.models

import com.datelocator.datelocatorbe.review.models.Review
import com.datelocator.datelocatorbe.user.models.User
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "venues")
data class Venue(
    @Id
    val id: UUID = UUID.randomUUID(),

    val googlePlacesId: String,
    val name: String,
    val lat: Double,
    val lng: Double,

    @OneToMany(mappedBy = "venue", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val reviews: MutableSet<Review> = mutableSetOf(),

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "opening_hours_id", nullable = true)
    val openingHours: OpeningHours? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val createdBy: User? = null,

    val createdAt: Instant = Instant.now()
) {
    fun addReview(review: Review) {
        reviews.add(review)
        review.venue = this
    }
}
