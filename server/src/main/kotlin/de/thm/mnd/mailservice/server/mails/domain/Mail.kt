package de.thm.mnd.mailservice.server.mails.domain


import de.thm.mnd.mailservice.server.user.domain.User
import de.thm.mnd.mailservice.server.attachment.domain.Attachment
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID


@Entity
@Table(name = "mails")
class Mail(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @Column(nullable = false)
    var subject: String = "",

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String = "",

    @Column(nullable = false)
    var fromEmail: String = "",

    @ElementCollection
    @CollectionTable(name = "mail_receivers", joinColumns = [JoinColumn(name = "mail_id")])
    var receiver: MutableList<String> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "mail_cc", joinColumns = [JoinColumn(name = "mail_id")])
    var carbonCopy: MutableList<String> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "mail_bcc", joinColumns = [JoinColumn(name = "mail_id")])
    var blindCarbonCopy: MutableList<String> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "mail_reply_to", joinColumns = [JoinColumn(name = "mail_id")])
    var replyTo: MutableList<String> = mutableListOf(),

    @Enumerated(EnumType.STRING)
    var status: MailStatus = MailStatus.DRAFT,

    @Enumerated(EnumType.STRING)
    @Column(updatable = false)
    val source: MailSource = MailSource.INTERN,

    @Column(updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now(),

    var sentAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    var sender: User,

    @OneToMany(
        mappedBy = "mail",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var attachments: MutableList<Attachment> = mutableListOf()
)

enum class MailStatus { DRAFT, SENT, ERROR }
enum class MailSource { INTERN, EXTERN }