package de.thm.mnd.mailservice.server.user.dto

data class UserResponse(
    val id: java.util.UUID,
    val firstName: String,
    val lastName: String,
    val email: String
)