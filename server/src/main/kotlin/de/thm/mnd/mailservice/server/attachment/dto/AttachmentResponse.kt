package de.thm.mnd.mailservice.server.attachment.dto

data class AttachmentResponse(
    val id: java.util.UUID,
    val fileName: String,
    val mimeType: String,
    val size: Long
)
