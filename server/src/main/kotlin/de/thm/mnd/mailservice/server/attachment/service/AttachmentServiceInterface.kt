package de.thm.mnd.mailservice.server.attachment.service

import de.thm.mnd.mailservice.server.attachment.dto.AttachmentResponse
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

interface AttachmentServiceInterface {
    fun getAttachmentMetadata(userId: UUID, mailId: UUID, attachmentId: UUID): AttachmentResponse
    fun uploadToMail(userId: UUID, mailId: UUID, file: MultipartFile): AttachmentResponse
    fun deleteAttachment(userId: UUID, mailId: UUID, attachmentId: UUID)
}