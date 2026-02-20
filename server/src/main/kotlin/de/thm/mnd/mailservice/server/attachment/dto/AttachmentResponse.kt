package de.thm.mnd.mailservice.server.attachment.dto

import de.thm.mnd.mailservice.server.attachment.domain.Attachment

data class AttachmentResponse(
    val id: java.util.UUID,
    val fileName: String,
    val mimeType: String,
    val size: Long
)
fun Attachment.toResponse(): AttachmentResponse =
    AttachmentResponse(
        id = this.id!!,
        fileName = this.fileName,
        mimeType = this.mimeType,
        size = this.size
    )
