package com.datelocator.datelocatorbe.votes

import com.datelocator.datelocatorbe.preference.models.Preference
import com.datelocator.datelocatorbe.user.models.User
import com.datelocator.datelocatorbe.venue.models.Venue
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "venue_preference_votes")
data class VenuePreferenceVote(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "venue_id")
    val venue: Venue,

    @ManyToOne
    @JoinColumn(name = "preference_id")
    val preference: Preference,

    var voteCount: Int = 1,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User
)
