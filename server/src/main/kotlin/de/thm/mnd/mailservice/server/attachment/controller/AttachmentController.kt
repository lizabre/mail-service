package de.thm.mnd.mailservice.server.attachment.controller

import de.thm.mnd.mailservice.server.attachment.dto.AttachmentResponse
import de.thm.mnd.mailservice.server.attachment.service.AttachmentServiceInterface
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/mails/{mailId}/attachments")
class AttachmentController(private val attachmentService: AttachmentServiceInterface) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadToMail(authentication: Authentication, @PathVariable mailId: UUID, @RequestParam("file") file: MultipartFile): AttachmentResponse {
        val userId = UUID.fromString(authentication.name)
        return attachmentService.uploadToMail(userId, mailId, file)
    }

    @GetMapping("/{attachmentId}")
    fun getAttachmentMetadata(authentication: Authentication, @PathVariable mailId: UUID, @PathVariable attachmentId: UUID): AttachmentResponse {
        val userId = UUID.fromString(authentication.name)
        return attachmentService.getAttachmentMetadata(userId, mailId, attachmentId)
    }

    @DeleteMapping("/{attachmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAttachment(authentication: Authentication, @PathVariable mailId: UUID, @PathVariable attachmentId: UUID) {
        val userId = UUID.fromString(authentication.name)
        attachmentService.deleteAttachment(userId, mailId, attachmentId)
    }
}