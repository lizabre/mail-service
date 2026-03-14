package de.thm.mnd.mailservice.server.mail.service

import de.thm.mnd.mailservice.server.mail.domain.Mail
import de.thm.mnd.mailservice.server.mail.dto.CreateMailRequest
import de.thm.mnd.mailservice.server.mail.dto.UpdateMailRequest
import java.util.UUID

interface MailServiceInterface {
    fun create(userId: UUID, request: CreateMailRequest): Mail
    fun sendMailDraft(userId: UUID, mailId: UUID): Mail
    fun updateMail(userId: UUID, mailId: UUID, request: UpdateMailRequest): Mail
    fun deleteMail(userId: UUID, mailId: UUID)
    fun getMailById(userId: UUID, mailId: UUID): Mail
    fun getInboxMails(userId: UUID): List<Mail>
    fun getSentMails(userId: UUID): List<Mail>
    fun getDraftMails(userId: UUID): List<Mail>
}