package com.datelocator.datelocatorbe.image.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Date
import java.util.UUID

@Entity
@Table(name = "images")
class Image(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val imageUrl: String,

    @Column(nullable = false)
    val entityId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val entityType: EntityType,

    @Column(nullable = false)
    val createdAt: Date
)
