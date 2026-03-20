package de.thm.mnd.mailservice.server

import de.thm.mnd.mailservice.server.attachment.domain.Attachment
import de.thm.mnd.mailservice.server.attachment.repository.AttachmentRepository
import de.thm.mnd.mailservice.server.mail.domain.Mail
import de.thm.mnd.mailservice.server.mail.repository.MailRepository
import de.thm.mnd.mailservice.server.shared.MailStatus
import de.thm.mnd.mailservice.server.user.domain.User
import de.thm.mnd.mailservice.server.user.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Seeds the database with initial sample data on application startup.
 * Creates test users and sample mails in various states.
 * All seeded users have the password: Password1!
 * These are mock credentials for development and testing purposes only.
 **/

@Component
class DataSeeder(
    private val userRepository: UserRepository,
    private val mailRepository: MailRepository,
    private val attachmentRepository: AttachmentRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {
    override fun run(vararg args: String) {
        if (userRepository.count() > 0) return

        // ── Users ──────────────────────────────────────────────
        val alice = userRepository.save(User(
            first_name = "Alice",
            last_name = "Smith",
            email = "alice@test.com",
            password = passwordEncoder.encode("Password1!")!!
        ))
        val bob = userRepository.save(User(
            first_name = "Bob",
            last_name = "Jones",
            email = "bob@test.com",
            password = passwordEncoder.encode("Password1!")!!
        ))
        val carol = userRepository.save(User(
            first_name = "Carol",
            last_name = "White",
            email = "carol@test.com",
            password = passwordEncoder.encode("Password1!")!!
        ))
        val dave = userRepository.save(User(
            first_name = "Dave",
            last_name = "Brown",
            email = "dave@test.com",
            password = passwordEncoder.encode("Password1!")!!
        ))

        // ── Story: project kickoff thread ──────────────────────

        // alice starts the project thread
        val kickoff = mailRepository.save(Mail(
            subject = "Project Kickoff - Action Items",
            content = """
                Hi team,
                
                Great kickoff meeting today! Here are the action items:
                - Bob: Set up the repository by Friday
                - Carol: Write the initial requirements doc
                - Dave: Schedule stakeholder interviews
                
                Let's sync again next Monday.
                
                Alice
            """.trimIndent(),
            receiver = mutableListOf(bob.email, carol.email, dave.email),
            carbonCopy = mutableListOf(),
            blindCarbonCopy = mutableListOf(),
            replyTo = mutableListOf(alice.email),
            status = MailStatus.SENT,
            sentAt = LocalDateTime.now().minusDays(5),
            updatedAt = LocalDateTime.now().minusDays(5),
            sender = alice
        ))

        // attach a fake meeting notes PDF
        attachmentRepository.save(Attachment(
            fileName = "meeting-notes.pdf",
            mimeType = "application/pdf",
            size = 24576,
            content = ByteArray(24576),
            mail = kickoff
        ))

        // bob replies with repo link
        val repoMail = mailRepository.save(Mail(
            subject = "Re: Project Kickoff - Repository Ready",
            content = """
                Hi Alice,
                
                Repository is set up and ready. I've added everyone as collaborators.
                Please find the setup guide attached.
                
                Bob
            """.trimIndent(),
            receiver = mutableListOf(alice.email),
            carbonCopy = mutableListOf(carol.email, dave.email),
            blindCarbonCopy = mutableListOf(),
            replyTo = mutableListOf(),
            status = MailStatus.SENT,
            sentAt = LocalDateTime.now().minusDays(4),
            updatedAt = LocalDateTime.now().minusDays(4),
            sender = bob
        ))

        attachmentRepository.save(Attachment(
            fileName = "setup-guide.pdf",
            mimeType = "application/pdf",
            size = 10240,
            content = ByteArray(10240),
            mail = repoMail
        ))

        // carol sends requirements doc
        val requirementsMail = mailRepository.save(Mail(
            subject = "Requirements Document v1.0",
            content = """
                Team,
                
                Please find the initial requirements document attached.
                I've highlighted the open questions in yellow — need input from everyone by Wednesday.
                
                Carol
            """.trimIndent(),
            receiver = mutableListOf(alice.email, bob.email, dave.email),
            carbonCopy = mutableListOf(),
            blindCarbonCopy = mutableListOf(),
            replyTo = mutableListOf(carol.email),
            status = MailStatus.SENT,
            sentAt = LocalDateTime.now().minusDays(3),
            updatedAt = LocalDateTime.now().minusDays(3),
            sender = carol
        ))

        attachmentRepository.save(Attachment(
            fileName = "requirements-v1.docx",
            mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            size = 35840,
            content = ByteArray(35840),
            mail = requirementsMail
        ))

        attachmentRepository.save(Attachment(
            fileName = "wireframes.png",
            mimeType = "image/png",
            size = 102400,
            content = ByteArray(102400),
            mail = requirementsMail
        ))

        // dave sends stakeholder interview schedule
        mailRepository.save(Mail(
            subject = "Stakeholder Interview Schedule",
            content = """
                Hi Alice,
                
                I've scheduled the stakeholder interviews:
                - Monday 10:00 - Marketing team
                - Tuesday 14:00 - Sales team  
                - Wednesday 11:00 - Executive sponsor
                
                I'll send calendar invites separately.
                
                Dave
            """.trimIndent(),
            receiver = mutableListOf(alice.email),
            carbonCopy = mutableListOf(bob.email, carol.email),
            blindCarbonCopy = mutableListOf(),
            replyTo = mutableListOf(),
            status = MailStatus.SENT,
            sentAt = LocalDateTime.now().minusDays(2),
            updatedAt = LocalDateTime.now().minusDays(2),
            sender = dave
        ))

        // alice sends a private update to bob only, bcc dave
        mailRepository.save(Mail(
            subject = "Confidential: Budget Concerns",
            content = """
                Bob,
                
                Between us — the budget for Q2 has been cut by 20%.
                We need to discuss how this affects the project scope before telling the team.
                
                Can we meet tomorrow morning?
                
                Alice
            """.trimIndent(),
            receiver = mutableListOf(bob.email),
            carbonCopy = mutableListOf(),
            blindCarbonCopy = mutableListOf(dave.email),
            replyTo = mutableListOf(),
            status = MailStatus.SENT,
            sentAt = LocalDateTime.now().minusHours(8),
            updatedAt = LocalDateTime.now().minusHours(8),
            sender = alice
        ))

        // ── Drafts ─────────────────────────────────────────────

        // alice drafting a status report
        mailRepository.save(Mail(
            subject = "Weekly Status Report - Week 10",
            content = """
                Team,
                
                Here is the weekly status update:
                
                Completed:
                - [TODO: fill in]
                
                In Progress:
                - [TODO: fill in]
                
                Blockers:
                - [TODO: fill in]
            """.trimIndent(),
            receiver = mutableListOf(bob.email, carol.email, dave.email),
            carbonCopy = mutableListOf(),
            blindCarbonCopy = mutableListOf(),
            replyTo = mutableListOf(),
            status = MailStatus.DRAFT,
            sender = alice
        ))

        // bob drafting a reply to carol
        mailRepository.save(Mail(
            subject = "Re: Requirements Document v1.0",
            content = """
                Carol,
                
                Thanks for sharing. My comments:
                1. Section 3.2 needs more detail
                2. The timeline in section 5 seems optimistic
                
                [TODO: add more feedback]
            """.trimIndent(),
            receiver = mutableListOf(carol.email),
            carbonCopy = mutableListOf(),
            blindCarbonCopy = mutableListOf(),
            replyTo = mutableListOf(),
            status = MailStatus.DRAFT,
            sender = bob
        ))
    }
}