package de.thm.mnd.mailservice.server.mail.service

import de.thm.mnd.mailservice.server.mail.domain.Mail
import de.thm.mnd.mailservice.server.mail.dto.CreateMailRequest
import de.thm.mnd.mailservice.server.mail.dto.MailResponse
import de.thm.mnd.mailservice.server.mail.dto.UpdateMailRequest
import de.thm.mnd.mailservice.server.mail.dto.toResponseFor
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
class MailService(private val mailRepository: MailRepository, private val userRepository: UserRepository, private val mailValidator: MailValidator) : MailServiceInterface {

    override fun create(userId: UUID, request: CreateMailRequest): MailResponse {

        val user = userRepository.findById(userId)
            .orElseThrow { MailAccessDeniedException("User not found") }

        val mail = Mail(
            subject = request.subject ?: "",
            content = request.content ?: "",
            receiver = request.receiver.toMutableList(),
            carbonCopy = request.carbonCopy.toMutableList(),
            blindCarbonCopy = request.blindCarbonCopy.toMutableList(),
            replyTo = request.replyTo.toMutableList(),
            sender = user,
            status = MailStatus.DRAFT
        )

        return mailRepository.save(mail).toResponseFor(userId)
    }

    override fun sendMailDraft(userId: UUID, mailId: UUID): MailResponse {
        val mail = getOwnedMail(userId, mailId)

        if (mail.status != MailStatus.DRAFT) {
            throw IllegalMailStateException("Mail already sent")
        }

        val errors = mailValidator.validateBeforeSend(mail)
        if (errors.isNotEmpty()) {
            throw IllegalMailStateException(errors.joinToString(", "))
        }

        mail.status = MailStatus.SENT
        mail.sentAt = LocalDateTime.now()
        mail.updatedAt = LocalDateTime.now()

        return mailRepository.save(mail).toResponseFor(userId)
    }

    override fun updateMail(userId: UUID, mailId: UUID, request: UpdateMailRequest): MailResponse {
        val mail = getOwnedMail(userId, mailId)

        if (mail.status != MailStatus.DRAFT) {
            throw IllegalMailStateException("Only drafts can be edited")
        }

        mail.subject = request.subject
        mail.content = request.content

        mail.receiver.clear()
        mail.receiver.addAll(request.receiver)

        mail.carbonCopy.clear()
        mail.carbonCopy.addAll(request.carbonCopy)

        mail.blindCarbonCopy.clear()
        mail.blindCarbonCopy.addAll(request.blindCarbonCopy)

        mail.replyTo.clear()
        mail.replyTo.addAll(request.replyTo)

        mail.updatedAt = LocalDateTime.now()

        return mailRepository.save(mail).toResponseFor(userId)
    }

    override fun deleteMail(userId: UUID, mailId: UUID) {
        val mail = getOwnedMail(userId, mailId)
        mailRepository.delete(mail)
    }

    override fun getMailById(userId: UUID, mailId: UUID): MailResponse =
        getOwnedMail(userId, mailId).toResponseFor(userId)

    override fun getSentMails(userId: UUID): List<MailResponse> =
        mailRepository
            .findBySenderIdAndStatus(userId, MailStatus.SENT)
            .sortedByDescending { it.createdAt }
            .map { it.toResponseFor(userId) }

    override fun getDraftMails(userId: UUID): List<MailResponse> =
        mailRepository
            .findBySenderIdAndStatus(userId, MailStatus.DRAFT)
            .sortedByDescending { it.createdAt }
            .map { it.toResponseFor(userId) }

    override fun getInboxMails(userId: UUID): List<MailResponse> {
        val user = userRepository.findById(userId)
            .orElseThrow { MailAccessDeniedException("User not found") }

        val email = user.email
        val result = mutableSetOf<Mail>()

        result += mailRepository.findByStatusAndReceiverContains(MailStatus.SENT, email)
        result += mailRepository.findByStatusAndCarbonCopyContains(MailStatus.SENT, email)
        result += mailRepository.findByStatusAndBlindCarbonCopyContains(MailStatus.SENT, email)

        return result
            .sortedByDescending { it.createdAt }
            .map { it.toResponseFor(userId) }
    }

    private fun getOwnedMail(userId: UUID, mailId: UUID): Mail {
        val mail = mailRepository.findById(mailId)
            .orElseThrow { MailNotFoundException("Mail not found") }

        if (mail.sender.id != userId) {
            throw MailAccessDeniedException("Not allowed")
        }

        return mail
    }
}