package de.thm.mnd.mailservice.server.mail.dto

import de.thm.mnd.mailservice.server.attachment.dto.AttachmentResponse
import de.thm.mnd.mailservice.server.shared.MailSource
import de.thm.mnd.mailservice.server.shared.MailStatus
import de.thm.mnd.mailservice.server.user.dto.UserResponse

data class MailResponse(
    val id: java.util.UUID,
    val subject: String,
    val content: String,

    val receiver: List<String>,
    val carbonCopy: List<String>,
    var blindCarbonCopy: List<String>,
    val replyTo: List<String>,

    val status: MailStatus,
    val source: MailSource,

    val createdAt: java.time.LocalDateTime,
    val updatedAt: java.time.LocalDateTime?,
    val sentAt: java.time.LocalDateTime?,
    val sender: UserResponse?,
    val attachments: List<AttachmentResponse> = emptyList()
)
