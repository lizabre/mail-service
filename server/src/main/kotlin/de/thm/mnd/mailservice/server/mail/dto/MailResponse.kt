package de.thm.mnd.mailservice.server.mail.dto

import de.thm.mnd.mailservice.server.attachment.dto.AttachmentResponse
import de.thm.mnd.mailservice.server.attachment.dto.toResponse
import de.thm.mnd.mailservice.server.mail.domain.Mail
import de.thm.mnd.mailservice.server.shared.MailSource
import de.thm.mnd.mailservice.server.shared.MailStatus
import de.thm.mnd.mailservice.server.user.dto.UserSummaryResponse
import de.thm.mnd.mailservice.server.user.dto.toUserSummary
import java.util.UUID

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
    val sender: UserSummaryResponse,
    val attachments: List<AttachmentResponse> = emptyList()
)

fun Mail.toResponseFor(userId: UUID): MailResponse {

    val isSender = this.sender.id == userId

    return MailResponse(
        id = this.id!!,
        subject = this.subject,
        content = this.content,
        receiver = this.receiver,
        carbonCopy = this.carbonCopy,
        blindCarbonCopy = if (isSender) this.blindCarbonCopy else emptyList(),
        replyTo = this.replyTo,
        status = this.status,
        source = this.source,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        sentAt = this.sentAt,
        sender = this.sender.toUserSummary(),
        attachments = this.attachments.map { it.toResponse() }
    )
}