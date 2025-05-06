package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.venue.models.Venue
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface VenueRepository : JpaRepository<Venue, UUID>{
}