package de.thm.mnd.mailservice.server.user.dto

import de.thm.mnd.mailservice.server.user.server.domain.UserAuthResult

data class UserResponse(
    val id: java.util.UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val token: String
)
fun UserAuthResult.toUserResponse(): UserResponse = UserResponse(
    id = this.user.id as java.util.UUID,
    firstName = this.user.first_name,
    lastName = this.user.last_name,
    email = this.user.email,
    token = this.token
)