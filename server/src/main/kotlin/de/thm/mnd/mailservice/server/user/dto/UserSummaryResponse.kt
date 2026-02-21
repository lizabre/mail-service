package de.thm.mnd.mailservice.server.user.dto

import de.thm.mnd.mailservice.server.user.domain.User
import java.util.UUID

data class UserSummaryResponse(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String
)

fun User.toUserSummary(): UserSummaryResponse =
    UserSummaryResponse(
        id = this.id!!,
        firstName = this.first_name,
        lastName = this.last_name,
        email = this.email
    )