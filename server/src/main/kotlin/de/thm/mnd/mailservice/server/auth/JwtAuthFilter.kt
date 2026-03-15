package de.thm.mnd.mailservice.server.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filter that processes JWT authentication for every incoming HTTP request.
 * Extracts and validates the Bearer token from the Authorization header.
 */
@Component
class JwtAuthFilter(private val jwtService: JwtService) : OncePerRequestFilter() {

    /**
     * Extracts the JWT token from the Authorization header and sets
     * the authentication in the security context if the token is valid.
     * @param request The incoming HTTP request.
     * @param response The HTTP response.
     * @param filterChain The filter chain to continue processing.
     */
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val header = request.getHeader("Authorization")

        if (header != null && header.startsWith("Bearer ")) {
            val token = header.substring(7)
            runCatching { jwtService.extractUserId(token) }
                .onSuccess { userId ->
                    SecurityContextHolder.getContext().authentication =
                        UsernamePasswordAuthenticationToken(userId.toString(), null, emptyList())
                }
        }

        filterChain.doFilter(request, response)
    }
}