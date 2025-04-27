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
)