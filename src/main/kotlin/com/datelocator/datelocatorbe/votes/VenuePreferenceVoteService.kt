package com.datelocator.datelocatorbe.votes

import org.springframework.stereotype.Service
import java.util.*

@Service
class VenuePreferenceVoteService (
    private val venuePreferenceVoteRepository: VenuePreferenceVoteRepository
) {
    fun getPreferenceIdsByVenueId(venueId: UUID): Set<UUID> {
        val preferenceVotes = venuePreferenceVoteRepository.findByVenueId(venueId)
        return preferenceVotes.map { it.venue.id}.toSet()
    }
}