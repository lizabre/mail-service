package de.thm.mnd.mailservice.server.attachment.service

import de.thm.mnd.mailservice.server.attachment.domain.Attachment
import de.thm.mnd.mailservice.server.attachment.dto.AttachmentResponse
import de.thm.mnd.mailservice.server.attachment.repository.AttachmentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class AttachmentService(private val attachmentRepository: AttachmentRepository) {

    fun getAttachmentMetadata(id: UUID): AttachmentResponse {
        val attachment = findAttachmentOrThrow(id)

        return AttachmentResponse(
            id = attachment.id ?: throw IllegalStateException("Attachment ID must not be null"),
            fileName = attachment.fileName,
            mimeType = attachment.mimeType,
            size = attachment.size
        )
    }

    fun getAllAttachments(): List<AttachmentResponse> {
        return attachmentRepository.findAll()
            .map { attachment ->
                AttachmentResponse(
                    id = attachment.id
                        ?: throw IllegalStateException("Attachment ID must not be null"),
                    fileName = attachment.fileName,
                    mimeType = attachment.mimeType,
                    size = attachment.size
                )
            }
    }

    @Transactional
    fun deleteAttachment(id: UUID) {
        val attachment = findAttachmentOrThrow(id)
        attachmentRepository.delete(attachment)
    }

    @Transactional
    fun processUpload(file: MultipartFile): AttachmentResponse {

        validateFile(file)

        val attachment = Attachment(
            fileName = file.originalFilename?.takeIf { it.isNotBlank() } ?: "unnamed_file",
            mimeType = file.contentType ?: "application/octet-stream",
            size = file.size,
            content = file.bytes
        )

        val saved = attachmentRepository.save(attachment)

        return AttachmentResponse(
            id = saved.id ?: throw IllegalStateException("Saved attachment has no ID"),
            fileName = saved.fileName,
            mimeType = saved.mimeType,
            size = saved.size
        )
    }

    private fun validateFile(file: MultipartFile) {
        if (file.isEmpty) {
            throw IllegalArgumentException("Cannot upload an empty file")
        }

        if (file.size <= 0) {
            throw IllegalArgumentException("File size must be greater than 0")
        }

        if (file.size > 10_000_000) {
            throw IllegalArgumentException("File exceeds maximum allowed size (10MB)")
        }
    }

    private fun findAttachmentOrThrow(id: UUID): Attachment {
        return attachmentRepository.findById(id)
            .orElseThrow { NoSuchElementException("Attachment with id $id not found") }
    }
}
