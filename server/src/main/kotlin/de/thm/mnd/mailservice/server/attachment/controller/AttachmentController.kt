package de.thm.mnd.mailservice.server.attachment.controller

import de.thm.mnd.mailservice.server.attachment.dto.AttachmentResponse
import de.thm.mnd.mailservice.server.attachment.service.AttachmentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/attachments")
class AttachmentController(private val attachmentService: AttachmentService) {

    @GetMapping("/{id}")
    fun getAttachmentMetadata(@PathVariable id: UUID): AttachmentResponse {
        return attachmentService.getAttachmentMetadata(id)
    }

    @GetMapping
    fun getAllAttachments(): List<AttachmentResponse> {
        return attachmentService.getAllAttachments()
    }

    /**
     * The request must be sent as `multipart/form-data` and must contain
     * exactly one form field with the name **"file"**.
     * IMPORTANT:
     * The form field name must match the value defined in @RequestParam("file").
     * If a different field name is used (e.g., "photo" or "attachment"),
     * the server will return HTTP 400 (Bad Request).
     */
    @PostMapping
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<AttachmentResponse> {
        val response = attachmentService.processUpload(file)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeAttachment(@PathVariable id: UUID) {
        attachmentService.deleteAttachment(id)
    }
}
