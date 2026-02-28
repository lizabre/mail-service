import {Component, OnInit} from '@angular/core';
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {InputField} from '../../components/input-field/input-field';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf, NgForOf} from '@angular/common';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {ChipField} from '../../components/chip-field/chip-field';
import {emailArrayValidator} from '../../utils/emailValidator';
import {MailService} from '../../api/mails-service/mails.service';
import {AttachmentService} from '../../api/attachment-service/attachment.service';
import {MatDialog} from '@angular/material/dialog';
import {Dialog} from '../../components/dialog/dialog';

@Component({
  selector: 'app-new-mail',
  imports: [
    MatButton,
    MatIcon,
    RouterLink,
    InputField,
    MatFormField,
    MatLabel,
    ReactiveFormsModule,
    MatError,
    MatInput,
    NgIf,
    NgForOf,
    MatMenuTrigger,
    MatMenu,
    MatMenuItem,
    ChipField,
    MatIconButton
  ],
  templateUrl: './new-mail.html',
  styleUrl: './new-mail.css',
})
export class NewMail implements OnInit {
  form: FormGroup;
  showCc = false;
  showBcc = false;
  showReplyTo = false;
  selectedFileName: string = '';
  selectedFiles: File[] = [];
  isSending = false;
  draftId: string | null = null;

  constructor(
    private fb: FormBuilder,
    private mailService: MailService,
    private attachmentService: AttachmentService,
    private router: Router,
    private route: ActivatedRoute,
    private dialog: MatDialog
  ) {
    this.form = this.fb.group({
      to: [[], [Validators.required, emailArrayValidator()]],
      cc: [[], emailArrayValidator()],
      bcc: [[], emailArrayValidator()],
      replyTo: [[], emailArrayValidator()],
      subject: ['', Validators.required],
      body: ['', Validators.required],
      attachments: []
    });
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      const mailId = params.get('id');
      if (mailId) {
        this.draftId = mailId;
        this.mailService.getMailById(mailId).subscribe({
          next: (draft) => {
            if (draft.carbonCopy.length > 0) this.showCc = true;
            if (draft.blindCarbonCopy.length > 0) this.showBcc = true;
            if (draft.replyTo.length > 0) this.showReplyTo = true;

            this.form.patchValue({
              to: draft.receiver,
              cc: draft.carbonCopy,
              bcc: draft.blindCarbonCopy,
              replyTo: draft.replyTo,
              subject: draft.subject,
              body: draft.content
            });
          },
          error: (err: unknown) => this.showError('Failed to load draft', this.getErrorMessage(err))
        });
      }
    });
  }

  get toControl() { return this.form.get('to') as FormControl; }
  get ccControl() { return this.form.get('cc') as FormControl; }
  get bccControl() { return this.form.get('bcc') as FormControl; }
  get replyToControl() { return this.form.get('replyTo') as FormControl; }
  get subjectControl() { return this.form.get('subject') as FormControl; }
  get bodyControl() { return this.form.get('body') as FormControl; }
  get attachmentsControl() { return this.form.get('attachments') as FormControl; }

  private showError(title: string, message: string): void {
    this.dialog.open(Dialog, {data: {title, message}});
  }

  private getErrorMessage(err: unknown): string {
    if (err && typeof err === 'object' && 'error' in err) {
      const error = (err as {error: {message?: string, errors?: string}}).error;
      if (error.message) return error.message;
      if (error.errors) return error.errors;
    }
    return 'An unexpected error occurred. Please try again.';
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const newFiles = Array.from(input.files);
      this.selectedFiles = [...this.selectedFiles, ...newFiles];
      this.attachmentsControl.setValue(this.selectedFiles);
      this.updateFileNames();
    }
  }

  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
    this.attachmentsControl.setValue(this.selectedFiles);
    this.updateFileNames();
  }

  triggerFileInput(): void {
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    fileInput.click();
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSending = true;

    if (this.draftId) {
      this.mailService.updateMail(this.draftId, {
        subject: this.subjectControl.value,
        content: this.bodyControl.value,
        receiver: this.toControl.value,
        carbonCopy: this.ccControl.value ?? [],
        blindCarbonCopy: this.bccControl.value ?? [],
        replyTo: this.replyToControl.value ?? []
      }).subscribe({
        next: () => {
          if (this.selectedFiles.length > 0) {
            this.uploadAttachmentsAndSend(this.draftId!);
          } else {
            this.sendDraft(this.draftId!);
          }
        },
        error: (err: unknown) => {
          this.isSending = false;
          this.showError('Failed to update mail', this.getErrorMessage(err));
        }
      });
    } else {
      this.mailService.createMail({
        subject: this.subjectControl.value,
        content: this.bodyControl.value,
        receiver: this.toControl.value,
        carbonCopy: this.ccControl.value ?? [],
        blindCarbonCopy: this.bccControl.value ?? [],
        replyTo: this.replyToControl.value ?? []
      }).subscribe({
        next: (draft) => {
          if (this.selectedFiles.length > 0) {
            this.uploadAttachmentsAndSend(draft.id);
          } else {
            this.sendDraft(draft.id);
          }
        },
        error: (err: unknown) => {
          this.isSending = false;
          this.showError('Failed to create mail', this.getErrorMessage(err));
        }
      });
    }
  }

  private uploadAttachmentsAndSend(mailId: string): void {
    let completed = 0;
    this.selectedFiles.forEach(file => {
      this.attachmentService.uploadAttachment(mailId, file).subscribe({
        next: () => {
          completed++;
          if (completed === this.selectedFiles.length) {
            this.sendDraft(mailId);
          }
        },
        error: (err: unknown) => {
          this.isSending = false;
          this.showError('Failed to upload attachment', `"${file.name}": ${this.getErrorMessage(err)}`);
        }
      });
    });
  }

  private sendDraft(mailId: string): void {
    this.mailService.sendMail(mailId).subscribe({
      next: () => {
        this.isSending = false;
        this.router.navigate(['/']);
      },
      error: (err: unknown) => {
        this.isSending = false;
        this.showError('Failed to send mail', this.getErrorMessage(err));
      }
    });
  }

  private updateFileNames(): void {
    this.selectedFileName = this.selectedFiles.map(f => f.name).join(', ');
  }
}
