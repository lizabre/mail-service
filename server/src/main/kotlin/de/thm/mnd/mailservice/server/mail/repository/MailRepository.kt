package de.thm.mnd.mailservice.server.mail.repository

import de.thm.mnd.mailservice.server.mail.domain.Mail
import de.thm.mnd.mailservice.server.shared.MailStatus
import de.thm.mnd.mailservice.server.user.domain.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MailRepository : CrudRepository<Mail, UUID> {
    fun findBySenderIdAndStatus(senderId: UUID, status: MailStatus): List<Mail>
    fun findByStatusAndReceiverContains(status: MailStatus, email: String): List<Mail>
    fun findByStatusAndCarbonCopyContains(status: MailStatus, email: String): List<Mail>
    fun findByStatusAndBlindCarbonCopyContains(status: MailStatus, email: String): List<Mail>
}