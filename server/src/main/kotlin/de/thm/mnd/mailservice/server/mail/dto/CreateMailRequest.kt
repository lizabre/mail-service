package de.thm.mnd.mailservice.server.mail.dto

data class CreateMailRequest(
    val subject: String,
    val content: String,
    val receiver: List<String>,
    val carbonCopy: List<String> = emptyList(),
    val blindCarbonCopy: List<String> = emptyList(),
    val replyTo: List<String> = emptyList()
)

