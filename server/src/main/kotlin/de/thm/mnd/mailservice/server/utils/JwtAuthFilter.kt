package de.thm.mnd.mailservice.server.utils

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val header = request.getHeader("Authorization")

        if (header != null && header.startsWith("Bearer ")) {

            val token = header.substring(7)

            try {
                val userId = jwtService.extractUserId(token)

                val authentication = UsernamePasswordAuthenticationToken(
                    userId.toString(),
                    null,
                    emptyList()
                )

                SecurityContextHolder.getContext().authentication = authentication

            } catch (ex: Exception) {
                // invalid token -> do nothing (request will be rejected later)
            }
        }

        filterChain.doFilter(request, response)
    }
}