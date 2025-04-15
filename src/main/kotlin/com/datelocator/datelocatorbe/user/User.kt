package com.datelocator.datelocatorbe.user

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User (
    @Id
    val firebaseUid: String,

    val username: String? = null,

    val firstName: String? = null,

    val lastName: String? = null,

    val gender: Genders? = null,

    val preferenceGender: Genders? = null,

    val age: Int? = null,

    val createdAt: LocalDateTime = LocalDateTime.now()
)