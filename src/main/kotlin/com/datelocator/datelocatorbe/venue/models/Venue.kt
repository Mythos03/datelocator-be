package com.datelocator.datelocatorbe.venue.models

import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.review.models.Review
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

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
    var reviews: MutableSet<Review> = HashSet(),

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "opening_hours_id", nullable = true)
    val openingHours: OpeningHours? = null,

    val createdAt: Instant = Instant.now()
){
    fun addReview(review: Review) {
        reviews.add(review)
        review.venue = this
    }
}
