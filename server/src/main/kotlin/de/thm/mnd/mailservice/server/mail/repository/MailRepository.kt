package de.thm.mnd.mailservice.server.mail.repository

import de.thm.mnd.mailservice.server.mail.domain.Mail
import de.thm.mnd.mailservice.server.shared.MailStatus
import de.thm.mnd.mailservice.server.user.domain.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MailRepository : CrudRepository<Mail, UUID> {
    fun findAllBySender(sender: User): List<Mail>
    fun findByIdAndSender(id: UUID, sender: User): List<Mail>
    fun findBySenderIdAndStatus(senderId: UUID, status: MailStatus): List<Mail>
}