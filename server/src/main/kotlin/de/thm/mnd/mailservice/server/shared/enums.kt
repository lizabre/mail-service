package de.thm.mnd.mailservice.server.shared

enum class MailStatus { DRAFT, SENT, ERROR }
enum class MailSource { INTERN, EXTERN }
enum class MailFolder { INBOX, SENT, DRAFTS }