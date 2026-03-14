package de.thm.mnd.mailservice.server.mail.dto

import de.thm.mnd.mailservice.server.mail.domain.Mail
import de.thm.mnd.mailservice.server.shared.MailStatus
import de.thm.mnd.mailservice.server.user.domain.User
import java.time.LocalDateTime
import java.util.UUID

data class UpdateMailRequest(
    val subject: String,
    val content: String,
    val receiver: List<String>,
    val carbonCopy: List<String> = emptyList(),
    val blindCarbonCopy: List<String> = emptyList(),
    val replyTo: List<String> = emptyList()
)
fun UpdateMailRequest.toMail(id: UUID, sender: User, status: MailStatus): Mail = Mail(
    id = id,
    subject = this.subject,
    content = this.content,
    receiver = this.receiver.toMutableList(),
    carbonCopy = this.carbonCopy.toMutableList(),
    blindCarbonCopy = this.blindCarbonCopy.toMutableList(),
    replyTo = this.replyTo.toMutableList(),
    sender = sender,
    status = status,
    updatedAt = LocalDateTime.now()
)