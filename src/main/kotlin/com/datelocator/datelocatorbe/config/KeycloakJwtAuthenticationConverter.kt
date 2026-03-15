package com.datelocator.datelocatorbe.config

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component
class KeycloakJwtAuthenticationConverter : Converter<Jwt, AbstractAuthenticationToken> {

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val authorities = extractResourceRoles(jwt)
        return JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt))
    }

    private fun extractResourceRoles(jwt: Jwt): Collection<GrantedAuthority> {
        val realmAccess = jwt.getClaim<Map<String, Any>>("realm_access") ?: return emptySet()
        val roles = realmAccess["roles"] as? List<*> ?: return emptySet()

        return roles.map { role -> SimpleGrantedAuthority("ROLE_$role") }.toSet()
    }

    private fun getPrincipalClaimName(jwt: Jwt): String {
        return jwt.getClaimAsString("preferred_username") ?: jwt.subject
    }
}
