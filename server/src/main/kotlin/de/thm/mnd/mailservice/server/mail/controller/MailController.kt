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

    /**
     * Creates a new mail draft.
     * @param authentication The authenticated user's security context.
     * @param request The mail creation payload.
     * @return The created [MailResponse] with status 201.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createMail(authentication: Authentication, @RequestBody request: CreateMailRequest): MailResponse {
        val userId = UUID.fromString(authentication.name)
        return mailService.create(userId, request).toResponseFor(userId)
    }

    /**
     * Sends a mail draft.
     * @param authentication The authenticated user's security context.
     * @param mailId The ID of the draft to send.
     * @return The updated [MailResponse].
     */
    @PostMapping("/{mailId}/send")
    fun sendMail(authentication: Authentication, @PathVariable mailId: UUID): MailResponse {
        val userId = UUID.fromString(authentication.name)
        return mailService.sendMailDraft(userId, mailId).toResponseFor(userId)
    }

    /**
     * Retrieves all mails in the specified folder.
     * @param authentication The authenticated user's security context.
     * @param folder The target folder (INBOX, SENT, DRAFTS).
     * @return List of [MailResponse] objects.
     */
    @GetMapping
    fun getMails(authentication: Authentication, @RequestParam folder: MailFolder): List<MailResponse> {
        val userId = UUID.fromString(authentication.name)

        return when (folder) {
            MailFolder.INBOX -> mailService.getInboxMails(userId).toResponseFor(userId)
            MailFolder.SENT -> mailService.getSentMails(userId).toResponseFor(userId)
            MailFolder.DRAFTS -> mailService.getDraftMails(userId).toResponseFor(userId)
        }
    }

    /**
     * Retrieves a single mail by ID.
     * @param authentication The authenticated user's security context.
     * @param mailId The ID of the mail to retrieve.
     * @return The [MailResponse] for the requested mail.
     */
    @GetMapping("/{mailId}")
    fun getMail(authentication: Authentication, @PathVariable mailId: UUID): MailResponse {
        val userId = UUID.fromString(authentication.name)
        return mailService.getMailById(userId, mailId).toResponseFor(userId)
    }

    /**
     * Updates an existing draft. Sent mails cannot be modified.
     * @param authentication The authenticated user's security context.
     * @param mailId The ID of the draft to update.
     * @param request The updated mail payload.
     * @return The updated [MailResponse].
     */
    @PutMapping("/{mailId}")
    fun updateMail(authentication: Authentication, @PathVariable mailId: UUID, @RequestBody request: UpdateMailRequest): MailResponse {
        val userId = UUID.fromString(authentication.name)
        return mailService.updateMail(userId, mailId, request).toResponseFor(userId)
    }

    /**
     * Permanently deletes a mail and its attachments.
     * @param authentication The authenticated user's security context.
     * @param mailId The ID of the mail to delete.
     */
    @DeleteMapping("/{mailId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMail(authentication: Authentication, @PathVariable mailId: UUID) {
        val userId = UUID.fromString(authentication.name)
        mailService.deleteMail(userId, mailId)
    }
}