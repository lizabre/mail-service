import {ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {MatIconButton, MatMiniFabButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {Attachment} from '../../components/attachment/attachment';
import {MailService} from '../../api/mails-service/mails.service';
import {AttachmentService} from '../../api/attachment-service/attachment.service';
import {MailResponse} from '../../api/mails-service/mails.models';
import {NgForOf, NgIf} from '@angular/common';
import {Dialog} from '../../components/dialog/dialog';
import {MatDialog} from '@angular/material/dialog';
import {formatDateTime} from '../../utils/formatDateTime';

/**
 * Page component for displaying a single mail in detail.
 * Handles mail deletion, attachment loading and attachment deletion.
 */
@Component({
  selector: 'app-mail',
  imports: [
    MatIcon,
    MatMiniFabButton,
    RouterLink,
    MatIconButton,
    Attachment,
    NgIf,
    NgForOf
  ],
  templateUrl: './mail.html',
  styleUrl: './mail.css',
})
export class Mail implements OnInit, OnDestroy {
  /** The currently loaded mail. */
  mail: MailResponse | null = null;

  /** Map of attachment ID to blob object URL for preview/download. */
  attachmentUrls = new Map<string, string>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private mailService: MailService,
    private attachmentService: AttachmentService,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog
  ) {}

  /**
   * Loads the mail by ID from the query params on component init.
   */
  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      const mailId = params.get('id');
      if (mailId) {
        this.mailService.getMailById(mailId).subscribe({
          next: (data) => {
            this.mail = data;
            this.cdr.detectChanges();
          },
          error: (err) => this.showError('Failed to load mail', `${this.getErrorMessage(err)}`)
        });
      }
    });
  }

  /**
   * Revokes all blob object URLs to free memory on component destroy.
   */
  ngOnDestroy(): void {
    this.attachmentUrls.forEach(url => URL.revokeObjectURL(url));
  }

  /**
   * Loads the binary content of an attachment and caches the object URL.
   * Skips loading if the URL is already cached.
   * @param mailId The ID of the mail.
   * @param attachmentId The ID of the attachment to load.
   */
  loadAttachment(mailId: string, attachmentId: string): void {
    if (this.attachmentUrls.has(attachmentId)) return;
    this.attachmentService.getAttachmentContent(mailId, attachmentId).subscribe(url => {
      this.attachmentUrls.set(attachmentId, url);
    });
  }

  /**
   * Deletes the current mail and navigates back to the home page.
   */
  onDelete(): void {
    if (!this.mail) return;
    this.mailService.deleteMail(this.mail.id).subscribe({
      next: () => this.router.navigate(['/']),
      error: (err) => {
        this.showError('Failed to delete an email', `${this.getErrorMessage(err)}`);
      }
    });
  }

  /**
   * Deletes an attachment from the current mail.
   * @param attachmentId The ID of the attachment to delete.
   */
  onDeleteAttachment(attachmentId: string): void {
    if (!this.mail) return;
    this.attachmentService.deleteAttachment(this.mail.id, attachmentId).subscribe({
      next: () => {
        this.mail!.attachments = this.mail!.attachments.filter(a => a.id !== attachmentId);
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.showError('Failed to delete attachment', `${this.getErrorMessage(err)}`);
      }
    });
  }

  /**
   * Opens an error dialog with the given title and message.
   * @param title The dialog title.
   * @param message The error message to display.
   */
  private showError(title: string, message: string): void {
    this.dialog.open(Dialog, {data: {title, message}});
  }

  /**
   * Extracts a human-readable error message from an HTTP error response.
   * @param err The unknown error object.
   * @returns The error message string.
   */
  private getErrorMessage(err: unknown): string {
    if (err && typeof err === 'object' && 'error' in err) {
      const error = (err as {error: {message?: string, errors?: string}}).error;
      if (error.message) return error.message;
      if (error.errors) return error.errors;
    }
    return 'An unexpected error occurred. Please try again.';
  }

  protected readonly formatDateTime = formatDateTime;
}
