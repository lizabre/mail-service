package de.thm.mnd.mailservice.server.mail.service

import de.thm.mnd.mailservice.server.mail.domain.Mail
import de.thm.mnd.mailservice.server.mail.dto.CreateMailRequest
import de.thm.mnd.mailservice.server.mail.dto.UpdateMailRequest
import de.thm.mnd.mailservice.server.mail.dto.toMail
import de.thm.mnd.mailservice.server.mail.repository.MailRepository
import de.thm.mnd.mailservice.server.mail.validation.MailValidator
import de.thm.mnd.mailservice.server.shared.MailStatus
import de.thm.mnd.mailservice.server.user.repository.UserRepository
import de.thm.mnd.mailservice.server.utils.exceptions.IllegalMailStateException
import de.thm.mnd.mailservice.server.utils.exceptions.MailAccessDeniedException
import de.thm.mnd.mailservice.server.utils.exceptions.MailNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class MailService(
    private val mailRepository: MailRepository,
    private val userRepository: UserRepository,
    private val mailValidator: MailValidator
) : MailServiceInterface {

    /**
     * Creates a new mail draft for the authenticated user.
     * @param userId The ID of the sender.
     * @param request The mail creation payload.
     * @return The saved [Mail] entity.
     */
    @Transactional
    override fun create(userId: UUID, request: CreateMailRequest): Mail {
        val user = userRepository.findById(userId)
            .orElseThrow { MailAccessDeniedException("User not found") }
        return mailRepository.save(request.toMail(user))
    }

    /**
     * Sends a mail draft after validating its content.
     * @param userId The ID of the sender.
     * @param mailId The ID of the draft to send.
     * @return The updated [Mail] entity with status SENT.
     * @throws IllegalMailStateException if the mail is not a draft or validation fails.
     */
    @Transactional
    override fun sendMailDraft(userId: UUID, mailId: UUID): Mail {
        val mail = getOwnedMail(userId, mailId)

        if (mail.status != MailStatus.DRAFT) {
            throw IllegalMailStateException("This mail has already been sent and cannot be sent again")
        }

        val errors = mailValidator.validateBeforeSend(mail)
        if (errors.isNotEmpty()) {
            throw IllegalMailStateException(errors.joinToString(", "))
        }

        mail.status = MailStatus.SENT
        mail.sentAt = LocalDateTime.now()
        mail.updatedAt = LocalDateTime.now()

        return mailRepository.save(mail)
    }

    /**
     * Updates an existing mail draft.
     * @param userId The ID of the sender.
     * @param mailId The ID of the draft to update.
     * @param request The updated mail payload.
     * @return The updated [Mail] entity.
     * @throws IllegalMailStateException if the mail is not a draft.
     */
    @Transactional
    override fun updateMail(userId: UUID, mailId: UUID, request: UpdateMailRequest): Mail {
        val mail = getOwnedMail(userId, mailId)
        if (mail.status != MailStatus.DRAFT) {
            throw IllegalMailStateException("Only draft mails can be edited. This mail has already been sent")
        }
        return mailRepository.save(request.toMail(mail.id as UUID, mail.sender, mail.status))
    }

    /**
     * Permanently deletes a mail and its attachments.
     * @param userId The ID of the sender.
     * @param mailId The ID of the mail to delete.
     */
    @Transactional
    override fun deleteMail(userId: UUID, mailId: UUID) {
        val mail = getOwnedMail(userId, mailId)
        mailRepository.delete(mail)
    }

    /**
     * Retrieves a single mail by ID.
     * Accessible by the sender and all recipients.
     * @param userId The ID of the requesting user.
     * @param mailId The ID of the mail to retrieve.
     * @return The [Mail] entity.
     * @throws MailAccessDeniedException if the user has no relation to the mail.
     * @throws MailNotFoundException if the mail does not exist.
     */
    override fun getMailById(userId: UUID, mailId: UUID): Mail {
        val user = userRepository.findById(userId)
            .orElseThrow { MailAccessDeniedException("User not found") }

        val mail = mailRepository.findById(mailId)
            .orElseThrow { MailNotFoundException("Mail not found") }

        val isSender = mail.sender.id == userId
        val isReceiver = mail.receiver.contains(user.email) ||
                mail.carbonCopy.contains(user.email) ||
                mail.blindCarbonCopy.contains(user.email)

        if (!isSender && !isReceiver) {
            throw MailAccessDeniedException("You do not have access to this mail")
        }

        return mail
    }

    /**
     * Retrieves all sent mails for a user, sorted by date descending.
     * @param userId The ID of the sender.
     * @return List of sent [Mail] entities.
     */
    override fun getSentMails(userId: UUID): List<Mail> =
        mailRepository
            .findBySenderIdAndStatus(userId, MailStatus.SENT)
            .sortedByDescending { it.createdAt }

    /**
     * Retrieves all draft mails for a user, sorted by date descending.
     * @param userId The ID of the sender.
     * @return List of draft [Mail] entities.
     */
    override fun getDraftMails(userId: UUID): List<Mail> =
        mailRepository
            .findBySenderIdAndStatus(userId, MailStatus.DRAFT)
            .sortedByDescending { it.createdAt }

    /**
     * Retrieves all received mails for a user from To, CC and BCC fields.
     * @param userId The ID of the recipient.
     * @return List of received [Mail] entities sorted by date descending.
     */
    override fun getInboxMails(userId: UUID): List<Mail> {
        val user = userRepository.findById(userId)
            .orElseThrow { MailAccessDeniedException("User not found") }

        val email = user.email
        val result = mutableSetOf<Mail>()

        result += mailRepository.findByStatusAndReceiverContains(MailStatus.SENT, email)
        result += mailRepository.findByStatusAndCarbonCopyContains(MailStatus.SENT, email)
        result += mailRepository.findByStatusAndBlindCarbonCopyContains(MailStatus.SENT, email)

        return result.sortedByDescending { it.createdAt }
    }

    private fun getOwnedMail(userId: UUID, mailId: UUID): Mail {
        val mail = mailRepository.findById(mailId)
            .orElseThrow { MailNotFoundException("Mail not found") }

        if (mail.sender.id != userId) {
            throw MailAccessDeniedException("You are not the sender of this mail")
        }

        return mail
    }
}