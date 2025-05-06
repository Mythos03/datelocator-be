package com.datelocator.datelocatorbe.votes

import com.datelocator.datelocatorbe.venue.models.Venue
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import java.util.UUID

@Repository
interface VenuePreferenceVoteRepository : JpaRepository<VenuePreferenceVote, UUID> {
    fun findByVenueId(venueId: UUID): List<VenuePreferenceVote>
    fun findByVenueIdAndPreferenceId(venueId: UUID, preferenceId: UUID): VenuePreferenceVote?
}