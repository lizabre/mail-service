package de.thm.mnd.mailservice.server.utils.exceptions

class InvalidValidationException(val errors: List<String>) : RuntimeException(errors.joinToString(", "))