package de.thm.mnd.mailservice.server.mail.service

import de.thm.mnd.mailservice.server.mail.dto.CreateMailRequest
import de.thm.mnd.mailservice.server.mail.dto.MailResponse
import de.thm.mnd.mailservice.server.mail.dto.UpdateMailRequest
import java.util.UUID

interface MailServiceInterface {
    fun create(userId: UUID, request: CreateMailRequest): MailResponse
    fun updateMail(userId: UUID, mailId: UUID, request: UpdateMailRequest): MailResponse
    fun sendMailDraft(userId: UUID, mailId: UUID): MailResponse
    fun getMailById(userId: UUID, mailId: UUID): MailResponse
    fun deleteMail(userId: UUID, mailId: UUID)
    fun getInboxMails(userId: UUID): List<MailResponse>
    fun getSentMails(userId: UUID): List<MailResponse>
    fun getDraftMails(userId: UUID): List<MailResponse>
}