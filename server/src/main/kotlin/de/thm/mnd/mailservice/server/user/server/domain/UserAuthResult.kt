package de.thm.mnd.mailservice.server.user.server.domain

import de.thm.mnd.mailservice.server.user.domain.User

data class UserAuthResult(
    val user: User,
    val token: String
)
