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
  mail: MailResponse | null = null;
  recipients: string[] = [];
  ccs: string[] = [];
  bccs: string[] = [];
  replyto: string[] = [];
  attachmentUrls = new Map<string, string>();


  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private mailService: MailService,
    private attachmentService: AttachmentService,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog
  ) {
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      const mailId = params.get('id');
      if (mailId) {
        this.mailService.getMailById(mailId).subscribe({
          next: (data) => {
            this.mail = data;
            this.cdr.detectChanges();
          },
          error: (err: unknown) => console.error('Failed to load mail', err)
        });
      }
    });
  }
  ngOnDestroy(): void {
    this.attachmentUrls.forEach(url => URL.revokeObjectURL(url));
  }

  loadAttachment(mailId: string, attachmentId: string): void {
    if (this.attachmentUrls.has(attachmentId)) return;

    this.attachmentService.getAttachmentContent(mailId, attachmentId).subscribe(url => {
      this.attachmentUrls.set(attachmentId, url);
    });
  }

  private showError(title: string, message: string): void {
    this.dialog.open(Dialog, {
      data: {title, message}
    });
  }

  private getErrorMessage(err: unknown): string {
    if (err && typeof err === 'object' && 'error' in err) {
      const error = (err as { error: { message?: string, errors?: string } }).error;
      if (error.message) return error.message;
      if (error.errors) return error.errors;
    }
    return 'An unexpected error occurred. Please try again.';
  }

  onDelete(): void {
    if (!this.mail) return;
    this.mailService.deleteMail(this.mail.id).subscribe({
      next: () => this.router.navigate(['/']),
      error: (err) => {
        this.showError('Failed to delete an email', `${this.getErrorMessage(err)}`);
      }
    });
  }

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



  protected readonly formatDateTime = formatDateTime;
}
