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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val preferences: List<Preference> = emptyList(),

    val age: Int? = null,

    val createdAt: LocalDateTime = LocalDateTime.now()
)