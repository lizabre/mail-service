package de.thm.mnd.mailservice.server.user.controller

import de.thm.mnd.mailservice.server.user.domain.User
import de.thm.mnd.mailservice.server.user.dto.UserLoginRequest
import de.thm.mnd.mailservice.server.user.dto.UserRegisterRequest
import de.thm.mnd.mailservice.server.user.dto.UserResponse
import de.thm.mnd.mailservice.server.user.dto.toUserResponse
import de.thm.mnd.mailservice.server.user.service.UserService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {
    @PostMapping("/register")
    fun registerUser(@RequestBody request: UserRegisterRequest): UserResponse {
        val userAuthReturn = userService.registerUser(request.firstName, request.lastName, request.email, request.password);
        return userAuthReturn.toUserResponse();
    }
    @PostMapping("/login")
    fun loginUser(@RequestBody request: UserLoginRequest): UserResponse {
        val userAuthReturn = userService.loginUser( request.email, request.password);
        return userAuthReturn.toUserResponse();
    }
    // Only for testing purposes, should not be exposed in production
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID): Boolean {
        return userService.deleteUser(id);
    }
    @GetMapping
    fun getAllUsers(): List<User> {
        return userService.getAllUsers();
    }
}