package es.atomsusej.tareaBackend.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date

@Service
class JwtService(
    @Value("\${security.jwt.secret}") private val secret: String,
    @Value("\${security.jwt.expiration-minutes}") private val expirationMinutes: Long
) {

    private val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    // Genro un tokken
    fun generateToken(user: UserDetails): String {

        val now = Instant.now()
        val exp = now.plusSeconds(expirationMinutes * 60)

        val roles = user.authorities.map { it.authority }

        return Jwts.builder()
            .subject(user.username)
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .claim("roles", roles)
            .signWith(key)
            .compact()
    }

    // Saca un usuario
    fun extractUsername(token: String): String {
        return extractAllClaims(token).subject
    }

    // Saca un rol
    fun extractRoles(token: String): List<String> {
        val claims = extractAllClaims(token)
        val roles = claims["roles"] ?: return emptyList()

        @Suppress("UNCHECKED_CAST")
        return if (roles is List<*>) {
            roles.filterIsInstance<String>()
        } else {
            emptyList()
        }
    }

    // Da el ok al tokken
    fun isTokenValid(token: String, user: UserDetails): Boolean {
        return try {
            val claims = extractAllClaims(token)
            val username = claims.subject
            val expired = claims.expiration.before(Date())

            username == user.username && !expired
        } catch (e: Exception) {
            false
        }
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}