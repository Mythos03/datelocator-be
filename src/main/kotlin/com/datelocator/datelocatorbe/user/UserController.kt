package com.datelocator.datelocatorbe.user

import com.datelocator.datelocatorbe.user.models.CreatePartialUserDto
import com.datelocator.datelocatorbe.user.models.UpdateProfilePictureDto
import com.datelocator.datelocatorbe.user.models.UpdateUserPreferencesRequest
import com.datelocator.datelocatorbe.user.models.UpdateUserRequestDto
import com.datelocator.datelocatorbe.user.models.UserRequestDto
import com.datelocator.datelocatorbe.user.models.UserResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val userMapper: UserMapper
) {

    @GetMapping("/firebase/{uid}")
    fun getUserByFirebaseUid(@PathVariable uid: String): ResponseEntity<UserResponseDto> {
        val user = userService.getUserById(uid)
        return if (user != null) ResponseEntity.ok(user)
        else ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createPartialUser(@RequestBody user: CreatePartialUserDto): ResponseEntity<UserResponseDto> {
        val savedUser = userService.createPartialUser(user)
        return ResponseEntity.ok(userMapper.toResponseDto(savedUser))
    }

    @PutMapping
    fun updatePartialUser(@RequestBody user: UserRequestDto): ResponseEntity<UserResponseDto> {
        val updatedUser = userService.updatePartialUser(user)
        return if (updatedUser != null) ResponseEntity.ok(updatedUser)
        else ResponseEntity.notFound().build()
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

    @PutMapping("/firebase/{uid}/preferences")
    fun updateUserPreferencesByFirebaseUid(
        @PathVariable uid: String,
        @RequestBody request: UpdateUserPreferencesRequest
    ): ResponseEntity<UserResponseDto> {
        val updatedUser = userService.updateUserPreferences(uid, request.preferences)
        return if (updatedUser != null) ResponseEntity.ok(updatedUser)
        else ResponseEntity.notFound().build()
    }

    @PutMapping("/{firebaseUid}")
    fun updateUser(
        @PathVariable firebaseUid: String,
        @RequestBody updateUserRequestDto: UpdateUserRequestDto
    ): ResponseEntity<UserResponseDto> {
        val updatedUser = userService.updateUser(firebaseUid, updateUserRequestDto)
        return if (updatedUser != null) ResponseEntity.ok(updatedUser)
        else ResponseEntity.notFound().build()
    }

    @PutMapping("/profile_picture/{firebaseUid}")
    fun updateProfilePicture(@PathVariable firebaseUid: String, @RequestBody updateProfilePictureDto: UpdateProfilePictureDto): ResponseEntity<UserResponseDto> {
        val updatedUser = userService.updateProfilePicture(firebaseUid, updateProfilePictureDto)
        return if (updatedUser != null) ResponseEntity.ok(updatedUser)
        else ResponseEntity.notFound().build()
    }
}
