package de.thm.mnd.mailservice.server.mail.controller

import de.thm.mnd.mailservice.server.mail.dto.CreateMailRequest
import de.thm.mnd.mailservice.server.mail.dto.MailResponse
import de.thm.mnd.mailservice.server.mail.dto.UpdateMailRequest
import de.thm.mnd.mailservice.server.mail.dto.toResponseFor
import de.thm.mnd.mailservice.server.mail.service.MailServiceInterface
import de.thm.mnd.mailservice.server.shared.MailFolder
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1.0/mails")
class MailController(private val mailService: MailServiceInterface) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createMail(authentication: Authentication, @RequestBody request: CreateMailRequest): MailResponse {
        val userId = UUID.fromString(authentication.name)
        return mailService.create(userId, request).toResponseFor(userId)
    }

    @PostMapping("/{mailId}/send")
    fun sendMail(authentication: Authentication, @PathVariable mailId: UUID): MailResponse {
        val userId = UUID.fromString(authentication.name)
        return mailService.sendMailDraft(userId, mailId).toResponseFor(userId)
    }

    @GetMapping
    fun getMails(authentication: Authentication, @RequestParam folder: MailFolder): List<MailResponse> {
        val userId = UUID.fromString(authentication.name)

        return when (folder) {
            MailFolder.INBOX -> mailService.getInboxMails(userId).toResponseFor(userId)
            MailFolder.SENT -> mailService.getSentMails(userId).toResponseFor(userId)
            MailFolder.DRAFTS -> mailService.getDraftMails(userId).toResponseFor(userId)
        }
    }

    @GetMapping("/{mailId}")
    fun getMail(authentication: Authentication, @PathVariable mailId: UUID): MailResponse {
        val userId = UUID.fromString(authentication.name)
        return mailService.getMailById(userId, mailId).toResponseFor(userId)
    }

    @PutMapping("/{mailId}")
    fun updateMail(authentication: Authentication, @PathVariable mailId: UUID, @RequestBody request: UpdateMailRequest): MailResponse {
        val userId = UUID.fromString(authentication.name)
        return mailService.updateMail(userId, mailId, request).toResponseFor(userId)
    }

    @DeleteMapping("/{mailId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMail(authentication: Authentication, @PathVariable mailId: UUID) {
        val userId = UUID.fromString(authentication.name)
        mailService.deleteMail(userId, mailId)
    }
}