package com.datelocator.datelocatorbe.image.models

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "images")
class Image(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val imageUrl: String,

    @Column(name = "entity_id",nullable = false)
    val entityId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val entityType: EntityType,
)
