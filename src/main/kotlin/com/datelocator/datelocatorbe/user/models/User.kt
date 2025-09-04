package com.datelocator.datelocatorbe.user.models

import com.datelocator.datelocatorbe.image.models.Image
import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.review.models.Review
import jakarta.persistence.*
import org.hibernate.annotations.Where
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    val firebaseUid: String,
    var username: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,

    @Enumerated(EnumType.ORDINAL)
    var gender: Genders? = null,

    @ManyToMany
    @JoinTable(
        name = "user_preferences",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "preference_id")]
    )
    var preferences: MutableSet<Preference> = mutableSetOf(),

    var age: Int? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val reviews: MutableSet<Review> = mutableSetOf(),

    var isComplete: Boolean = false,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(
        name = "entityId", // The column in the 'images' table
        referencedColumnName = "firebaseUid", // The column in this 'users' table (Note: it's not 'id')
        insertable = false,
        updatable = false
    )
    @Where(clause = "entityType = 'USER'") // Filter images for the 'USER' type
    var image: Image? = null

    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return firebaseUid == other.firebaseUid
    }

    override fun hashCode(): Int {
        return firebaseUid.hashCode()
    }
}