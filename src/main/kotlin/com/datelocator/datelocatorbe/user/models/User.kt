package com.datelocator.datelocatorbe.user.models

import com.datelocator.datelocatorbe.image.models.Image
import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.review.models.Review
import jakarta.persistence.*
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "keycloak_id" ,nullable = false, unique = true)
    val keycloakId: UUID,
    @Column(nullable = false, unique = true)
    var username: String? = null,

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

    @Column(nullable = false, columnDefinition = "boolean default false")
    var isComplete: Boolean = false,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "entity_type = 'USER'")
    var image: Image? = null


) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
