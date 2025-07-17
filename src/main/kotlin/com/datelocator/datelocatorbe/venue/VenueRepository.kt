package com.datelocator.datelocatorbe.venue

import com.datelocator.datelocatorbe.venue.models.Venue
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface VenueRepository : JpaRepository<Venue, UUID>{
    fun findVenueByGooglePlacesId(googlePlacesId: String): Venue?
    fun existsByGooglePlacesId(googlePlacesId: String): Boolean

    @Query(
        nativeQuery = true,
        value = """
      SELECT v.*, 
        6371 * acos(
          cos(radians(:lat)) *
          cos(radians(v.lat)) *
          cos(radians(v.lng) - radians(:lng)) +
          sin(radians(:lat)) * sin(radians(v.lat))
        ) AS distance
      FROM venues v
      JOIN reviews r ON r.venue_id = v.id
      JOIN venue_preference_votes vpv ON vpv.venue_id = v.id
      WHERE vpv.preference_id IN (:preferenceIds)
      GROUP BY v.id
      HAVING AVG(r.rating) >= :minRating
      ORDER BY distance
      LIMIT :limit OFFSET :offset
    """
    )
    fun findRecommendedVenuesByProximityAndPreferences(
        @Param("lat") lat: Double,
        @Param("lng") lng: Double,
        @Param("preferenceIds") preferenceIds: List<UUID>,
        @Param("minRating") minRating: Double = 4.0,
        @Param("limit") limit: Int,
        @Param("offset") offset: Int
    ): List<Venue>

    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Venue>
}
