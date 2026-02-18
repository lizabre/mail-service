package de.thm.mnd.mailservice.server.attachment.repository

import de.thm.mnd.mailservice.server.attachment.domain.Attachment
import de.thm.mnd.mailservice.server.mail.domain.Mail
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID


@Repository
interface AttachmentRepository : CrudRepository<Attachment, UUID> {
    fun findAllByMail(mail: Mail): List<Attachment>
    fun deleteAllByMail(mail: Mail)
}