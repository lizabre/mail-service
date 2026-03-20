package de.thm.mnd.mailservice.server.attachment.service

import de.thm.mnd.mailservice.server.attachment.domain.Attachment
import de.thm.mnd.mailservice.server.attachment.repository.AttachmentRepository
import de.thm.mnd.mailservice.server.attachment.validation.AttachmentValidator
import de.thm.mnd.mailservice.server.mail.domain.Mail
import de.thm.mnd.mailservice.server.mail.repository.MailRepository
import de.thm.mnd.mailservice.server.shared.MailStatus
import de.thm.mnd.mailservice.server.user.repository.UserRepository
import de.thm.mnd.mailservice.server.utils.exceptions.IllegalMailStateException
import de.thm.mnd.mailservice.server.utils.exceptions.MailAccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

/**
 * Service for managing mail attachments.
 * Handles uploading, retrieving, and deleting attachments for mails.
 */
@Service
class AttachmentService(
    private val attachmentRepository: AttachmentRepository,
    private val mailRepository: MailRepository,
    private val userRepository: UserRepository,
    private val attachmentValidator: AttachmentValidator
) : AttachmentServiceInterface {

    /**
     * Retrieves an attachment by ID, verifying the user has access to the mail.
     * @param userId The ID of the requesting user.
     * @param mailId The ID of the mail containing the attachment.
     * @param attachmentId The ID of the attachment to retrieve.
     * @return The [Attachment] entity.
     * @throws MailAccessDeniedException if the user has no access to the mail.
     */
    override fun getAttachment(userId: UUID, mailId: UUID, attachmentId: UUID): Attachment {
        val user = userRepository.findById(userId)
            .orElseThrow { MailAccessDeniedException("User not found") }

        val mail = mailRepository.findById(mailId)
            .orElseThrow { IllegalArgumentException("Mail not found") }

        validateMailAccess(userId, user.email, mail)

        return mail.attachments
            .firstOrNull { it.id == attachmentId }
            ?: throw IllegalArgumentException("Attachment not found in this mail")
    }

    /**
     * Uploads a file as an attachment to a mail draft.
     * @param userId The ID of the requesting user (must be the sender).
     * @param mailId The ID of the draft mail.
     * @param file The multipart file to attach.
     * @return The saved [Attachment] entity.
     * @throws MailAccessDeniedException if the user is not the sender.
     * @throws IllegalMailStateException if the mail is not a draft or file validation fails.
     */
    override fun uploadToMail(userId: UUID, mailId: UUID, file: MultipartFile): Attachment {
        val mail = mailRepository.findById(mailId)
            .orElseThrow { IllegalArgumentException("Mail not found") }

        if (mail.sender.id != userId) {
            throw MailAccessDeniedException("Only the sender can add attachments to a mail")
        }

        if (mail.status != MailStatus.DRAFT) {
            throw IllegalMailStateException("Attachments cannot be added to a mail that has already been sent")
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
        return attachmentRepository.save(attachment)
    }

    /**
     * Deletes an attachment from a mail draft.
     * @param userId The ID of the requesting user (must be the sender).
     * @param mailId The ID of the draft mail.
     * @param attachmentId The ID of the attachment to delete.
     * @throws MailAccessDeniedException if the user is not the sender.
     * @throws IllegalMailStateException if the mail is not a draft.
     */
    override fun deleteAttachment(userId: UUID, mailId: UUID, attachmentId: UUID) {
        val mail = mailRepository.findById(mailId)
            .orElseThrow { IllegalArgumentException("Mail not found") }

        if (mail.sender.id != userId) {
            throw MailAccessDeniedException("Only the sender can delete attachments")
        }

        if (mail.status != MailStatus.DRAFT) {
            throw IllegalMailStateException("Attachments cannot be deleted from a mail that has already been sent")
        }

        val attachment = mail.attachments
            .firstOrNull { it.id == attachmentId }
            ?: throw IllegalArgumentException("Attachment not found in this mail")

        attachmentRepository.delete(attachment)
    }

    private fun validateMailAccess(userId: UUID, userEmail: String, mail: Mail) {
        val isSender = mail.sender.id == userId
        val isReceiver = mail.receiver.contains(userEmail) ||
                mail.carbonCopy.contains(userEmail) ||
                mail.blindCarbonCopy.contains(userEmail)

        if (!isSender && !isReceiver) {
            throw MailAccessDeniedException("Not allowed to access this mail")
        }
    }
}