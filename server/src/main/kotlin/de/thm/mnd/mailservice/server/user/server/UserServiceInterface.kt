package de.thm.mnd.mailservice.server.user.server

import de.thm.mnd.mailservice.server.user.domain.User
import de.thm.mnd.mailservice.server.user.server.domain.UserAuthResult
import java.util.UUID

interface UserServiceInterface {
    fun registerUser(firstName:String, lastName:String, email:String, rawPassword:String): UserAuthResult
    fun loginUser(email:String, rawPassword:String ): UserAuthResult
    // Only for testing purposes, not used in the actual application
    fun deleteUser(userId: UUID): Boolean
    fun getAllUsers(): List<User>
}