package de.thm.mnd.mailservice.server.user.dto

data class UserLoginRequest(
    val email: String,
    val password: String
)