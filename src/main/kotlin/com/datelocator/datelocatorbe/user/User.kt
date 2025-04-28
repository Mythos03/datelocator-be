package com.datelocator.datelocatorbe.user

import com.datelocator.datelocatorbe.preference.Preference
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    val firebaseUid: String,
    val username: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,

    @Enumerated(EnumType.ORDINAL)
    val gender: Genders? = null,

    @ManyToMany
    @JoinTable(
        name = "user_preferences",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "preference_id")]
    )
    var preferences: MutableSet<Preference> = mutableSetOf(),

    val age: Int? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
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