package de.thm.mnd.mailservice.server.attachment.service

import de.thm.mnd.mailservice.server.attachment.domain.Attachment
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

interface AttachmentServiceInterface {
    fun getAttachment(userId: UUID, mailId: UUID, attachmentId: UUID): Attachment
    fun uploadToMail(userId: UUID, mailId: UUID, file: MultipartFile): Attachment
    fun deleteAttachment(userId: UUID, mailId: UUID, attachmentId: UUID)
}