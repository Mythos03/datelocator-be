package com.datelocator.datelocatorbe.user

import com.datelocator.datelocatorbe.user.models.UpdateUserPreferencesRequest
import com.datelocator.datelocatorbe.user.models.User
import com.datelocator.datelocatorbe.user.models.UserResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val userMapper: UserMapper
) {

    @GetMapping("/{uid}")
    fun getUserById(@PathVariable uid: String): ResponseEntity<UserResponseDto> {
        val user = userService.getUserById(uid)
        return if (user != null) ResponseEntity.ok(user)
        else ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<UserResponseDto> {
        val savedUser = userService.createUser(user)
        return ResponseEntity.ok(userMapper.toResponseDto(savedUser))
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserResponseDto>> {
        return ResponseEntity.ok(userService.getAllUsers())
    }

    @GetMapping("/username")
    fun findByUsername(@RequestParam username: String): ResponseEntity<UserResponseDto> {
        val user = userService.findByUsername(username)
        return if (user != null) ResponseEntity.ok(user)
        else ResponseEntity.notFound().build()
    }

    @PutMapping("/{uid}/preferences")
    fun updateUserPreferences(
        @PathVariable uid: String,
        @RequestBody request: UpdateUserPreferencesRequest
    ): ResponseEntity<UserResponseDto> {
        val updatedUser = userService.updateUserPreferences(uid, request.preferences)
        return if (updatedUser != null) ResponseEntity.ok(updatedUser)
        else ResponseEntity.notFound().build()
    }
}
