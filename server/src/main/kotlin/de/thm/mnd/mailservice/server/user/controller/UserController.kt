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
@RequestMapping("/api/v1.0/users")
class UserController(private val userService: UserService) {

    /**
     * Registers a new user account.
     * @param request Registration payload with name, email and password.
     * @return [UserResponse] containing the JWT token.
     */
    @PostMapping("/register")
    fun registerUser(@RequestBody request: UserRegisterRequest): UserResponse {
        val userAuthReturn = userService.registerUser(request.firstName, request.lastName, request.email, request.password);
        return userAuthReturn.toUserResponse();
    }

    /**
     * Authenticates a user and returns a JWT token.
     * @param request Login payload with email and password.
     * @return [UserResponse] containing the JWT token.
     */
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

    /**
     * Retrieves all registered users.
     * @return List of all [User] entities.
     */
    @GetMapping
    fun getAllUsers(): List<User> {
        return userService.getAllUsers();
    }
}