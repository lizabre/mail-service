import {Component} from '@angular/core';
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {Router, RouterLink} from "@angular/router";
import {InputField} from '../../components/input-field/input-field';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf, NgForOf} from '@angular/common';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {ChipField} from '../../components/chip-field/chip-field';
import {emailArrayValidator} from '../../utils/emailValidator';
import {MailService} from '../../api/mails-service/mails.service';
import {AttachmentService} from '../../api/attachment-service/attachment.service';

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
export class NewMail {
  form: FormGroup;
  showCc = false;
  showBcc = false;
  showReplyTo = false;
  selectedFileName: string = '';
  selectedFiles: File[] = [];
  isSending = false;

  constructor(
    private fb: FormBuilder,
    private mailService: MailService,
    private attachmentService: AttachmentService,
    private router: Router
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
  get toControl() { return this.form.get('to') as FormControl; }
  get ccControl() { return this.form.get('cc') as FormControl; }
  get bccControl() { return this.form.get('bcc') as FormControl; }
  get replyToControl() { return this.form.get('replyTo') as FormControl; }
  get subjectControl() { return this.form.get('subject') as FormControl; }
  get bodyControl() { return this.form.get('body') as FormControl; }
  get attachmentsControl() { return this.form.get('attachments') as FormControl; }

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
      error: (err) => {
        console.error('Failed to create draft', err);
        this.isSending = false;
      }
    });
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
         error: (err) => {
           console.error('Failed to upload attachment', err);
           this.isSending = false;
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
      error: (err) => {
        console.error('Failed to send mail', err);
        this.isSending = false;
      }
    });
  }
  private updateFileNames(): void {
    this.selectedFileName = this.selectedFiles.map(f => f.name).join(', ');
  }
}
