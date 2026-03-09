package de.thm.mnd.mailservice.server.mail.dto

import de.thm.mnd.mailservice.server.mail.domain.Mail
import de.thm.mnd.mailservice.server.shared.MailStatus
import de.thm.mnd.mailservice.server.user.domain.User
import java.util.UUID

data class CreateMailRequest(
    val subject: String,
    val content: String,
    val receiver: List<String>,
    val carbonCopy: List<String> = emptyList(),
    val blindCarbonCopy: List<String> = emptyList(),
    val replyTo: List<String> = emptyList()
)

fun CreateMailRequest.toMail(sender: User) =
    Mail(
        sender = sender,
        subject = this.subject ?: "",
        content = this.content ?: "",
        receiver = this.receiver.toMutableList(),
        carbonCopy = this.carbonCopy.toMutableList(),
        blindCarbonCopy = this.blindCarbonCopy.toMutableList(),
        replyTo = this.replyTo.toMutableList(),
        status = MailStatus.DRAFT
    )