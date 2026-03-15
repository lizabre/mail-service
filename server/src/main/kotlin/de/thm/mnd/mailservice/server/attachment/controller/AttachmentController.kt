package de.thm.mnd.mailservice.server.attachment.controller

import de.thm.mnd.mailservice.server.attachment.dto.AttachmentResponse
import de.thm.mnd.mailservice.server.attachment.dto.toResponse
import de.thm.mnd.mailservice.server.attachment.service.AttachmentServiceInterface
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/v1.0/mails/{mailId}/attachments")
class AttachmentController(private val attachmentService: AttachmentServiceInterface) {

    /**
     * Uploads a file as an attachment to a mail draft.
     * @param authentication The authenticated user's security context.
     * @param mailId The ID of the mail to attach the file to.
     * @param file The multipart file to upload.
     * @return The created [AttachmentResponse] with status 201.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadToMail(authentication: Authentication, @PathVariable mailId: UUID, @RequestParam("file") file: MultipartFile): AttachmentResponse {
        val userId = UUID.fromString(authentication.name)
        return attachmentService.uploadToMail(userId, mailId, file).toResponse()
    }

    /**
     * Retrieves metadata of a specific attachment.
     * @param authentication The authenticated user's security context.
     * @param mailId The ID of the mail.
     * @param attachmentId The ID of the attachment.
     * @return The [AttachmentResponse] with attachment metadata.
     */
    @GetMapping("/{attachmentId}")
    fun getAttachmentMetadata(authentication: Authentication, @PathVariable mailId: UUID, @PathVariable attachmentId: UUID): AttachmentResponse {
        val userId = UUID.fromString(authentication.name)
        return attachmentService.getAttachment(userId, mailId, attachmentId).toResponse()
    }

    /**
     * Downloads the binary content of an attachment.
     * @param authentication The authenticated user's security context.
     * @param mailId The ID of the mail.
     * @param attachmentId The ID of the attachment.
     * @return The file content as a [ByteArrayResource] with appropriate headers.
     */
    @GetMapping("/{attachmentId}/content")
    fun getAttachmentContent(authentication: Authentication, @PathVariable mailId: UUID, @PathVariable attachmentId: UUID): ResponseEntity<ByteArrayResource> {
        val userId = UUID.fromString(authentication.name)
        val attachment = attachmentService.getAttachment(userId, mailId, attachmentId)

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(attachment.mimeType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${attachment.fileName}\"")
            .contentLength(attachment.size)
            .body(ByteArrayResource(attachment.content))
    }

    /**
     * Deletes an attachment from a mail draft.
     * @param authentication The authenticated user's security context.
     * @param mailId The ID of the mail.
     * @param attachmentId The ID of the attachment to delete.
     */
    @DeleteMapping("/{attachmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAttachment(authentication: Authentication, @PathVariable mailId: UUID, @PathVariable attachmentId: UUID) {
        val userId = UUID.fromString(authentication.name)
        attachmentService.deleteAttachment(userId, mailId, attachmentId)
    }
}