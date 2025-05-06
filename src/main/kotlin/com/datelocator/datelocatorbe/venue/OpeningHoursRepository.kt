package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.venue.models.OpeningHours
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OpeningHoursRepository : JpaRepository<OpeningHours, UUID> {

}