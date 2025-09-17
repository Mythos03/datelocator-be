package com.datelocator.datelocatorbe.review

import com.datelocator.datelocatorbe.review.models.ReviewRequestDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
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
    private val logger = LoggerFactory.getLogger(ReviewController::class.java)

    @PostMapping
    fun createReview(@RequestBody reviewRequestDto: ReviewRequestDto): ResponseEntity<Any> {
        return try {
            logger.info("Received review creation request")
            val review = reviewService.createReview(reviewRequestDto)
            logger.info("Review created successfully")
            ResponseEntity.ok(review)
        } catch (e: IllegalArgumentException) {
            logger.error("Failed to create review: Bad request", e)
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Failed to create review", e)
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to e.message))
        }
    }
}
