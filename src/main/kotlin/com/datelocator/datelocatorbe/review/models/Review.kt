package com.datelocator.datelocatorbe.review.models

import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.user.models.User
import com.datelocator.datelocatorbe.venue.models.Venue
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

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
    val preferences: MutableSet<Preference> = mutableSetOf()
)