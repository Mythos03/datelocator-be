package com.datelocator.datelocatorbe.review

import com.datelocator.datelocatorbe.review.models.Review
import com.datelocator.datelocatorbe.review.models.ReviewRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/api/reviews")
class ReviewController (
    private val reviewService: ReviewService
){
    @PostMapping
    fun createReview(@RequestBody reviewRequestDto: ReviewRequestDto): ResponseEntity<Review> {
        return try {
            val review = reviewService.createReview(reviewRequestDto)
            ResponseEntity.ok(review)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
}