package de.thm.mnd.mailservice.server.attachment.service

import de.thm.mnd.mailservice.server.attachment.domain.Attachment
import de.thm.mnd.mailservice.server.attachment.dto.AttachmentResponse
import de.thm.mnd.mailservice.server.attachment.repository.AttachmentRepository
import de.thm.mnd.mailservice.server.attachment.validation.AttachmentValidator
import de.thm.mnd.mailservice.server.mail.domain.Mail
import de.thm.mnd.mailservice.server.mail.repository.MailRepository
import de.thm.mnd.mailservice.server.shared.MailStatus
import de.thm.mnd.mailservice.server.user.repository.UserRepository
import de.thm.mnd.mailservice.server.utils.exceptions.IllegalMailStateException
import de.thm.mnd.mailservice.server.utils.exceptions.MailAccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class AttachmentService(
    private val attachmentRepository: AttachmentRepository,
    private val mailRepository: MailRepository,
    private val userRepository: UserRepository,
    private val attachmentValidator: AttachmentValidator
): AttachmentServiceInterface {
    override fun getAttachmentMetadata(userId: UUID, mailId: UUID, attachmentId: UUID): AttachmentResponse {

        val user = userRepository.findById(userId)
            .orElseThrow { MailAccessDeniedException("User not found") }

        val mail = mailRepository.findById(mailId)
            .orElseThrow { IllegalArgumentException("Mail not found") }

        validateMailAccess(userId, user.email, mail)

        val attachment = mail.attachments
            .firstOrNull { it.id == attachmentId }
            ?: throw IllegalArgumentException("Attachment not found in this mail")

        return AttachmentResponse(
            id = attachment.id!!,
            fileName = attachment.fileName,
            mimeType = attachment.mimeType,
            size = attachment.size
        )
    }

    override fun uploadToMail(userId: UUID, mailId: UUID, file: MultipartFile): AttachmentResponse {
        val mail = mailRepository.findById(mailId)
            .orElseThrow { IllegalArgumentException("Mail not found") }

        if (mail.sender.id != userId) {
            throw MailAccessDeniedException("Not allowed to add attachment")
        }

        if (mail.status != MailStatus.DRAFT) {
            throw IllegalMailStateException("Cannot modify attachments of sent mail")
        }

        val errors = attachmentValidator.validate(file)
        if (errors.isNotEmpty()) {
            throw IllegalMailStateException(errors.joinToString(", "))
        }

        val attachment = Attachment(
            fileName = file.originalFilename ?: "unnamed_file",
            mimeType = file.contentType ?: "application/octet-stream",
            size = file.size,
            content = file.bytes,
            mail = mail
        )

        mail.attachments.add(attachment)

        val saved = attachmentRepository.save(attachment)

        return AttachmentResponse(
            id = saved.id!!,
            fileName = saved.fileName,
            mimeType = saved.mimeType,
            size = saved.size
        )
    }

    override fun deleteAttachment(userId: UUID, mailId: UUID, attachmentId: UUID) {
        val mail = mailRepository.findById(mailId)
            .orElseThrow { IllegalArgumentException("Mail not found") }

        if (mail.sender.id != userId) {
            throw MailAccessDeniedException("Not allowed to delete attachment")
        }

        if (mail.status != MailStatus.DRAFT) {
            throw IllegalMailStateException("Cannot delete attachment from sent mail")
        }

        val attachment = mail.attachments
            .firstOrNull { it.id == attachmentId }
            ?: throw IllegalArgumentException("Attachment not found in this mail")

        attachmentRepository.delete(attachment)
    }

    private fun validateMailAccess(userId: UUID, userEmail: String, mail: Mail) {

        val isSender = mail.sender.id == userId
        val isReceiver =
            mail.receiver.contains(userEmail) ||
                    mail.carbonCopy.contains(userEmail) ||
                    mail.blindCarbonCopy.contains(userEmail)

        if (!isSender && !isReceiver) {
            throw MailAccessDeniedException("Not allowed to access this mail")
        }
    }
}