package com.datelocator.datelocatorbe.image

import com.datelocator.datelocatorbe.image.models.Image
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ImageRepository : JpaRepository<Image, UUID> {
    fun getImagesByEntityId(entityId: UUID): MutableList<Image>
}