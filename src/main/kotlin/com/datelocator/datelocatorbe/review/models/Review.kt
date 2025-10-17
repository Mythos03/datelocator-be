package com.datelocator.datelocatorbe.review.models

import com.datelocator.datelocatorbe.image.models.Image
import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.user.models.User
import com.datelocator.datelocatorbe.venue.models.Venue
import jakarta.persistence.*
import org.hibernate.annotations.Where
import java.util.*

@Entity
@Table(name = "reviews")
data class Review(
    @Id
    val id: UUID = UUID.randomUUID(),

    val rating: Double,
    val reviewText: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    var venue: Venue? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToMany
    @JoinTable(
    name = "review_preferences",
    joinColumns = [JoinColumn(name = "review_id")],
    inverseJoinColumns = [JoinColumn(name = "preference_id")])
    val preferences: MutableSet<Preference> = mutableSetOf(),

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(
        name = "entity_id",
        referencedColumnName = "id",
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
        nullable = true
    )
    @Where(clause = "entity_type = 'REVIEW'")
    val images: MutableSet<Image>? = null
)