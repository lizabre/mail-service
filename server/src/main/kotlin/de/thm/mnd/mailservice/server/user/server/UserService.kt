package de.thm.mnd.mailservice.server.user.server

import de.thm.mnd.mailservice.server.user.domain.User
import de.thm.mnd.mailservice.server.user.repository.UserRepository
import de.thm.mnd.mailservice.server.user.server.domain.UserAuthResult
import de.thm.mnd.mailservice.server.utils.JwtService
import de.thm.mnd.mailservice.server.utils.UserValidator
import de.thm.mnd.mailservice.server.utils.exceptions.InvalidLoginCredentials
import de.thm.mnd.mailservice.server.utils.exceptions.InvalidValidationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(private val userRepository: UserRepository,
                  private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val userValidator: UserValidator) : UserServiceInterface {
    override fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        rawPassword: String
    ): UserAuthResult {
        val errors = userValidator.validateRegistrationData(firstName, lastName, email, rawPassword);

        if (errors.isNotEmpty()) {
            throw InvalidValidationException(errors)
        }

        val user = User(first_name = firstName, last_name=lastName, email = email, password = passwordEncoder.encode((rawPassword)) as String);
        userRepository.save(user);
        val token = jwtService.generateToken(user);
        return UserAuthResult(user, token);
    }

    override fun loginUser(email: String, rawPassword: String): UserAuthResult {
        val errors = userValidator.validateLoginData(email, rawPassword);
        if (errors.isNotEmpty()) {
            throw InvalidValidationException(errors)
        }
        val user = userRepository.findByEmail(email) ?: throw InvalidLoginCredentials("Invalid email or password");
        if(!passwordEncoder.matches(rawPassword, user.password)) {
            throw InvalidLoginCredentials("Invalid email or password");
        }
        val token = jwtService.generateToken(user);
        return UserAuthResult(user, token);
    }
    // Only for testing purposes, not recommended for production
    override fun deleteUser(userId: UUID): Boolean {
        val user = userRepository.findById(userId).orElse(null) ?: return false
        userRepository.delete(user);
        return true;
    }

    override fun getAllUsers(): List<User> {
        return userRepository.findAll().toList();
    }

}