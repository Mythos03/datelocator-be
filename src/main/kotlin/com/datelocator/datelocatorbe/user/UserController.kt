package com.datelocator.datelocatorbe.user

import com.datelocator.datelocatorbe.user.models.UpdateUserRequestDto
import com.datelocator.datelocatorbe.user.models.UserRequestDto
import com.datelocator.datelocatorbe.user.models.UserResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService, private val userMapper: UserMapper) {

    @PostMapping
    fun registerUser(
            @AuthenticationPrincipal jwt: Jwt,
            @RequestBody userRequestDto: UserRequestDto
    ): ResponseEntity<UserResponseDto> {
        val keycloakUserId = jwt.subject
        val existingUser = userService.getUserById(keycloakUserId)

        if (existingUser != null) {
            return ResponseEntity.ok(existingUser) // User already exists, return 200 OK
        }

        val requestWithTokenId = userRequestDto.copy(keycloakId = keycloakUserId)
        val newUser = userService.createUser(requestWithTokenId)
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponseDto(newUser))
    }

    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<UserResponseDto> {
        val keycloakUserId = jwt.subject
        val user = userService.getUserById(keycloakUserId)
        return if (user != null) ResponseEntity.ok(user) else ResponseEntity.notFound().build()
    }

    @PutMapping("/me")
    fun updateUser(
            @AuthenticationPrincipal jwt: Jwt,
            @RequestBody updateUserRequestDto: UpdateUserRequestDto
    ): ResponseEntity<UserResponseDto> {
        val keycloakUserId = jwt.subject

        val updatedUser = userService.updateUser(keycloakUserId, updateUserRequestDto)
        return if (updatedUser != null) ResponseEntity.ok(updatedUser)
        else ResponseEntity.notFound().build()
    }
}
