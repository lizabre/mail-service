package de.thm.mnd.mailservice.server.attachment.validation

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class AttachmentValidator {

    private val maxSizeBytes = 10_000_000L

    private val allowedMimeTypes = setOf(
        "application/pdf",
        "image/png",
        "image/jpeg",
        "text/plain",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    )

    fun validate(file: MultipartFile): List<String> {

        val errors = mutableListOf<String>()

        if (file.isEmpty) {
            errors.add("File must not be empty")
        }

        if (file.size <= 0) {
            errors.add("File size must be greater than 0")
        }

        if (file.size > maxSizeBytes) {
            errors.add("File exceeds maximum allowed size (10MB)")
        }

        val contentType = file.contentType

        if (contentType == null || contentType !in allowedMimeTypes) {
            errors.add("Unsupported file type")
        }

        return errors
    }
}