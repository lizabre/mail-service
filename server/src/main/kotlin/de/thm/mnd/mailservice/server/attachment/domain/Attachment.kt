package de.thm.mnd.mailservice.server.attachment.domain

import de.thm.mnd.mailservice.server.mail.domain.Mail
import jakarta.persistence.*
import java.util.UUID


@Entity
@Table(name = "attachments")
class Attachment(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @Column(nullable = false)
    var fileName: String = "",

    @Column(nullable = false)
    var mimeType: String = "",

    @Column(nullable = false)
    var size: Long = 0,

    @Lob
    @Column(columnDefinition = "BLOB", nullable = false)
    var content: ByteArray = byteArrayOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mail_id", nullable = false)
    var mail: Mail
)