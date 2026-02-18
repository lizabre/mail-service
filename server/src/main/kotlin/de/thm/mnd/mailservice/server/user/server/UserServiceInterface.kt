package de.thm.mnd.mailservice.server.user.server

import de.thm.mnd.mailservice.server.user.server.domain.UserAuthResult

interface UserServiceInterface {
    fun registerUser(firstName:String, lastName:String, email:String, rawPassword:String): UserAuthResult
    fun loginUser(email:String, rawPassword:String ): UserAuthResult
}