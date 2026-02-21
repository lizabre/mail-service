package de.thm.mnd.mailservice.server.mail.controller

import de.thm.mnd.mailservice.server.mail.dto.CreateMailRequest
import de.thm.mnd.mailservice.server.mail.dto.MailResponse
import de.thm.mnd.mailservice.server.mail.dto.UpdateMailRequest
import de.thm.mnd.mailservice.server.mail.service.MailServiceInterface
import de.thm.mnd.mailservice.server.shared.MailFolder
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/mails")
class MailController(private val mailService: MailServiceInterface) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createMail(authentication: Authentication, @RequestBody request: CreateMailRequest): MailResponse {
        val userId = UUID.fromString(authentication.name)
        return mailService.create(userId, request)
    }

    @PostMapping("/{mailId}/send")
    fun sendMail(authentication: Authentication, @PathVariable mailId: UUID): MailResponse {
        val userId = UUID.fromString(authentication.name)
        return mailService.sendMailDraft(userId, mailId)
    }

    @GetMapping
    fun getMails(authentication: Authentication, @RequestParam folder: MailFolder): List<MailResponse> {
        val userId = UUID.fromString(authentication.name)

        return when (folder) {
            MailFolder.INBOX -> mailService.getInboxMails(userId)
            MailFolder.SENT -> mailService.getSentMails(userId)
            MailFolder.DRAFTS -> mailService.getDraftMails(userId)
        }
    }

    @GetMapping("/{mailId}")
    fun getMail(authentication: Authentication, @PathVariable mailId: UUID): MailResponse {
        val userId = UUID.fromString(authentication.name)
        return mailService.getMailById(userId, mailId)
    }

    @PutMapping("/{mailId}")
    fun updateMail(authentication: Authentication, @PathVariable mailId: UUID, @RequestBody request: UpdateMailRequest): MailResponse {
        val userId = UUID.fromString(authentication.name)
        return mailService.updateMail(userId, mailId, request)
    }

    @DeleteMapping("/{mailId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMail(authentication: Authentication, @PathVariable mailId: UUID) {
        val userId = UUID.fromString(authentication.name)
        mailService.deleteMail(userId, mailId)
    }
}