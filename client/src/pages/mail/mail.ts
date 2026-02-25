import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {MatIconButton, MatMiniFabButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {Attachment} from '../../components/attachment/attachment';
import {MailService} from '../../api/mails-service/mails.service';
import {AttachmentService} from '../../api/attachment-service/attachment.service';
import {MailResponse} from '../../api/mails-service/mails.models';
import {NgForOf, NgIf} from '@angular/common';

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
export class Mail implements OnInit {
  mail: MailResponse | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private mailService: MailService,
    private attachmentService: AttachmentService,
    private cdr: ChangeDetectorRef
  ) {}

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

  onDelete(): void {
    if (!this.mail) return;
    this.mailService.deleteMail(this.mail.id).subscribe({
      next: () => this.router.navigate(['/']),
      error: (err: unknown) => console.error('Failed to delete mail', err)
    });
  }

  onDeleteAttachment(attachmentId: string): void {
    if (!this.mail) return;
    this.attachmentService.deleteAttachment(this.mail.id, attachmentId).subscribe({
      next: () => {
        this.mail!.attachments = this.mail!.attachments.filter(a => a.id !== attachmentId);
        this.cdr.detectChanges();
      },
      error: (err: unknown) => console.error('Failed to delete attachment', err)
    });
  }
}
