package de.thm.mnd.mailservice.server.auth

import de.thm.mnd.mailservice.server.user.domain.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

/**
 * Service for generating and parsing JSON Web Tokens.
 * Uses HMAC-SHA256 signing with a configurable secret key.
 */
@Service
class JwtService(@Value("\${jwt.secret}") private val secret: String) {

    private val expirationMs = 1000 * 60 * 60

    private val secretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    /**
     * Generates a signed JWT token for the given user.
     * @param user The [User] to generate the token for.
     * @return A signed JWT token string.
     */
    fun generateToken(user: User): String =
        Jwts.builder()
            .setSubject(user.id.toString())
            .claim("email", user.email)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expirationMs))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()

    fun extractUserId(token: String): UUID = UUID.fromString(
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
                .subject
        )
}