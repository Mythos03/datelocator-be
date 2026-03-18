package com.datelocator.datelocatorbe.config

import com.datelocator.datelocatorbe.security.UserSyncFilter
import com.datelocator.datelocatorbe.user.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
        private val keycloakJwtAuthenticationConverter: KeycloakJwtAuthenticationConverter,
        private val userRepository: UserRepository,
        @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
        private val jwkSetUri: String,
        @Value("\${app.security.jwt.accepted-issuers}")
        private val acceptedIssuersProperty: String
) {

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val acceptedIssuers = acceptedIssuersProperty
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toSet()

        val issuerValidator = OAuth2TokenValidator<Jwt> { jwt ->
            val issuer = jwt.issuer?.toString()
            if (issuer != null && acceptedIssuers.contains(issuer)) {
                OAuth2TokenValidatorResult.success()
            } else {
                OAuth2TokenValidatorResult.failure(
                        OAuth2Error(
                                "invalid_token",
                                "The iss claim is not valid",
                                null
                        )
                )
            }
        }

        val jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()
        jwtDecoder.setJwtValidator(
                DelegatingOAuth2TokenValidator(
                        JwtValidators.createDefault(),
                        issuerValidator
                )
        )
        return jwtDecoder
    }

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
