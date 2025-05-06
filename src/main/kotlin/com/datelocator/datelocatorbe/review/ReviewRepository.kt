package com.datelocator.datelocatorbe.review

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ReviewRepository : JpaRepository<Review, UUID> {
}