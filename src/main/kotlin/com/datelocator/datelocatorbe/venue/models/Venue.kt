package com.datelocator.datelocatorbe.venue.models

import com.datelocator.datelocatorbe.image.models.Image
import com.datelocator.datelocatorbe.review.models.Review
import com.datelocator.datelocatorbe.user.models.User
import jakarta.persistence.*
import org.hibernate.annotations.Where
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

    val createdAt: Instant = Instant.now(),

    @Column(name = "average_rating", nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0.0")
    var averageRating: Double = 0.0,

    @Column(name = "review_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    var reviewCount: Int = 0,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(
        name = "entityId", // The column in the 'images' table
        referencedColumnName = "id", // The column in this 'venues' table
        insertable = false,
        updatable = false
    )
    @Where(clause = "entityType = 'VENUE'") // Filter images for this specific entity type
    val images: MutableSet<Image> = mutableSetOf(),

) {
    fun addReview(review: Review) {
        reviews.add(review)
        review.venue = this
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Venue) return false
        return id == other.id
    }
}
