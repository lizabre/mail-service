export type MailFolder = 'INBOX' | 'SENT' | 'DRAFTS';

export type MailStatus = 'DRAFT' | 'SENT' | 'ERROR';

export interface AttachmentResponse {
  id: string;
  fileName: string;
  mimeType: string;
  size: number;
}

export interface MailResponse {
  id: string;
  subject: string;
  content: string;
  from: string;
  receiver: string[];
  carbonCopy: string[];
  blindCarbonCopy: string[];
  replyTo: string[];
  status: MailStatus;
  source: 'INTERN' | 'EXTERN';
  createdAt: string;
  updatedAt: string;
  sentAt: string | null;
  attachments: AttachmentResponse[];
}

export interface CreateMailRequest {
  subject: string;
  content: string;
  receiver: string[];
  carbonCopy: string[];
  blindCarbonCopy: string[];
  replyTo: string[];
}

export interface UpdateMailRequest {
  subject: string;
  content: string;
  receiver: string[];
  carbonCopy: string[];
  blindCarbonCopy: string[];
  replyTo: string[];
}
