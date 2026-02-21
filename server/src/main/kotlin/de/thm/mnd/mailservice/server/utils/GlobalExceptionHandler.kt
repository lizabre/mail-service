package de.thm.mnd.mailservice.server.utils

import de.thm.mnd.mailservice.server.utils.exceptions.EmailAlreadyExistsException
import de.thm.mnd.mailservice.server.utils.exceptions.IllegalMailStateException
import de.thm.mnd.mailservice.server.utils.exceptions.InvalidLoginCredentials
import de.thm.mnd.mailservice.server.utils.exceptions.InvalidValidationException
import de.thm.mnd.mailservice.server.utils.exceptions.MailAccessDeniedException
import de.thm.mnd.mailservice.server.utils.exceptions.MailNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(InvalidValidationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidValidation(ex: InvalidValidationException): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body(
            mapOf(
                "message" to "Validation failed",
                "errors" to ex.errors
            )
        )
    }
    @ExceptionHandler(EmailAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleEmailExists(ex: EmailAlreadyExistsException): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body(
            mapOf(
                "message" to "Email already exists",
                "errors" to ex.message
            )
        )
    }
    @ExceptionHandler(InvalidLoginCredentials::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidVLogin(ex: InvalidLoginCredentials): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body(
            mapOf(
                "message" to "Login failed",
                "errors" to ex.message
            )
        )
    }

    @ExceptionHandler(MailNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleMailNotFound(ex: MailNotFoundException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            mapOf(
                "message" to "Mail not found",
                "errors" to ex.message
            )
        )
    }

    @ExceptionHandler(IllegalMailStateException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalMailState(ex: IllegalMailStateException): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body(
            mapOf(
                "message" to "Invalid mail state",
                "errors" to ex.message
            )
        )
    }

    @ExceptionHandler(MailAccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDenied(ex: MailAccessDeniedException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            mapOf(
                "message" to "Access denied",
                "errors" to ex.message
            )
        )
    }
}