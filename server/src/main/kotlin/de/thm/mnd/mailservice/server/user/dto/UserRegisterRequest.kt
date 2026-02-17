package de.thm.mnd.mailservice.server.user.dto

data class UserRegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)
