package de.thm.mnd.mailservice.server.attachment.dto

data class AttachmentRequest(
    val fileName: String,
    val mimeType: String,
    val size: Long
)
