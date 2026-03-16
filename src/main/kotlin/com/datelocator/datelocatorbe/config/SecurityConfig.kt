package com.datelocator.datelocatorbe.config

import com.datelocator.datelocatorbe.security.UserSyncFilter
import com.datelocator.datelocatorbe.user.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
        private val keycloakJwtAuthenticationConverter: KeycloakJwtAuthenticationConverter,
        private val userRepository: UserRepository
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
                .csrf { it.disable() }
                .authorizeHttpRequests { auth ->
                    // Allow Swagger and open endpoints
                    auth.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                            .permitAll()
                    // Require a valid token for everything else
                    auth.anyRequest().authenticated()
                }
                .oauth2ResourceServer { oauth2 ->
                    oauth2.jwt { jwt ->
                        jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter)
                    }
                }
                .addFilterAfter(
                        UserSyncFilter(userRepository),
                        BearerTokenAuthenticationFilter::class.java
                )

        return http.build()
    }
}
