package com.datelocator.datelocatorbe.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.Scopes
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig(
    @Value("\${app.security.swagger.issuer-uri}")
    private val keycloakIssuerUri: String
) {

    companion object {
        private const val SECURITY_SCHEME_NAME = "keycloak_oauth"
    }

    @Bean
    fun customOpenAPI(): OpenAPI {
        val authUrl = "$keycloakIssuerUri/protocol/openid-connect/auth"
        val tokenUrl = "$keycloakIssuerUri/protocol/openid-connect/token"

        val oauthScheme = SecurityScheme()
            .type(SecurityScheme.Type.OAUTH2)
            .flows(
                OAuthFlows().authorizationCode(
                    OAuthFlow()
                        .authorizationUrl(authUrl)
                        .tokenUrl(tokenUrl)
                        .scopes(
                            Scopes()
                                .addString("openid", "OpenID Connect scope")
                                .addString("profile", "Profile scope")
                        )
                )
            )

        return OpenAPI()
            .info(
                Info()
                    .title("DateLocator API")
                    .version("1.0")
                    .description("API documentation for the DateLocator application")
            )
            .components(
                Components().addSecuritySchemes(SECURITY_SCHEME_NAME, oauthScheme)
            )
            .addSecurityItem(
                SecurityRequirement().addList(SECURITY_SCHEME_NAME, listOf("openid", "profile"))
            )
    }
}