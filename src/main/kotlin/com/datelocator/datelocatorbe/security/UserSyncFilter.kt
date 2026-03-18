package com.datelocator.datelocatorbe.security

import com.datelocator.datelocatorbe.user.UserRepository
import com.datelocator.datelocatorbe.user.models.User
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

class UserSyncFilter(
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication is JwtAuthenticationToken) {
            val jwt = authentication.token
            val keycloakIdStr = jwt.subject
            
            if (keycloakIdStr != null) {
                try {
                    val keycloakId = UUID.fromString(keycloakIdStr)
                    
                    // Check if user exists
                    val existingUser = userRepository.findByKeycloakId(keycloakId)
                    
                    if (existingUser == null) {
                        // Create a new user from JWT claims
                        val username = jwt.getClaimAsString("username")

                        val newUser = User(
                            keycloakId = keycloakId,
                            username = username,
                        )
                        
                        userRepository.save(newUser)
                    }
                } catch (e: Exception) {
                    // Log error but don't fail the request, auth is still valid
                    logger.error("Failed to sync user from Keycloak token", e)
                }
            }
        }

        filterChain.doFilter(request, response)
    }
}
