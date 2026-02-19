package de.thm.mnd.mailservice.server.utils

import org.springframework.stereotype.Component

@Component
class UserValidator {
    fun validateRegistrationData(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): List<String> {
        val errors = mutableListOf<String>()

        if (firstName.isBlank())
            errors.add("First name must not be empty")

        if (lastName.isBlank())
            errors.add("Last name must not be empty")

        errors.addAll(validateEmail(email))
        errors.addAll(validatePassword(password))

        return errors
    }
    fun validateLoginData(email: String, password: String): List<String> {
        val errors = mutableListOf<String>()

        errors.addAll(validateEmail(email))
        errors.addAll(validatePassword(password))

        return errors
    }
    private fun validateEmail(email: String): List<String> {
        val errors = mutableListOf<String>()

        if (email.isBlank()) {
            errors.add("Email must not be empty")
            return errors
        }

        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        if (!emailRegex.matches(email)) {
            errors.add("Email format is invalid")
        }

        return errors
    }
    private fun validatePassword(password: String): List<String> {
        val errors = mutableListOf<String>()

        if (password.length < 8)
            errors.add("Password must be at least 8 characters")

        if (!password.any { it.isUpperCase() })
            errors.add("Password must contain an uppercase letter")

        if (!password.any { it.isLowerCase() })
            errors.add("Password must contain a lowercase letter")

        if (!password.any { it.isDigit() })
            errors.add("Password must contain a digit")

        if (!password.any { !it.isLetterOrDigit() })
            errors.add("Password must contain a special character")

        return errors
    }
}