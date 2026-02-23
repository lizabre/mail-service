package de.thm.mnd.mailservice.server.mail.validation

import de.thm.mnd.mailservice.server.mail.domain.Mail
import org.springframework.stereotype.Component

@Component
class MailValidator {

    fun validateBeforeSend(mail: Mail): List<String> {
        val errors = mutableListOf<String>()

        if (mail.subject.isBlank()) {
            errors.add("Subject must not be empty")
        }

        if (mail.content.isBlank()) {
            errors.add("Content must not be empty")
        }

        val hasRecipient =
            mail.receiver.isNotEmpty() ||
                    mail.carbonCopy.isNotEmpty() ||
                    mail.blindCarbonCopy.isNotEmpty()

        if (!hasRecipient) {
            errors.add("At least one recipient required")
        }

        return errors
    }
}