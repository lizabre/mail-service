import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
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
import {formatDateTime} from '../../utils/formatDateTime';
import {catchError, forkJoin, map, Observable, of, switchMap, tap, throwError} from 'rxjs';

/**
 * Page component for composing and editing mail drafts.
 * Supports creating new mails, editing existing drafts,
 * uploading attachments, saving drafts and sending mails.
 */
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
  isSaving = false;
  draftId: string | null = null;
  updatedAt: string | null = null;

  constructor(
    private fb: FormBuilder,
    private mailService: MailService,
    private attachmentService: AttachmentService,
    private router: Router,
    private route: ActivatedRoute,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef
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

  /**
   * Loads an existing draft if an ID is present in the query params.
   * Pre-fills the form with the draft data.
   */
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
            this.updatedAt = draft.updatedAt;
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

  /**
   * Handles file selection and appends new files to the existing list.
   * @param event The file input change event.
   */
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const newFiles = Array.from(input.files);
      this.selectedFiles = [...this.selectedFiles, ...newFiles];
      this.attachmentsControl.setValue(this.selectedFiles);
      this.updateFileNames();
    }
  }

  /**
   * Removes a file from the selected files list by index.
   * @param index The index of the file to remove.
   */
  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
    this.attachmentsControl.setValue(this.selectedFiles);
    this.updateFileNames();
  }

  /** Programmatically triggers the hidden file input. */
  triggerFileInput(): void {
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    fileInput.click();
  }

  /**
   * Submits the form by saving the draft, uploading attachments and sending.
   * Shows an error dialog if any step fails.
   */
  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSending = true;
    this.saveMailDraft().pipe(
      switchMap(mailId => this.uploadAttachments(mailId).pipe(map(() => mailId))),
      switchMap(mailId => this.mailService.sendMail(mailId))
    ).subscribe({
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

  /**
   * Saves the current form state as a draft without sending.
   * Shows a success dialog on completion.
   */
  onSaveDraft(): void {
    this.isSaving = true;
    this.saveMailDraft().pipe(
      switchMap(mailId => this.uploadAttachments(mailId).pipe(
        tap(() => this.updateFileNames()),
        map(() => mailId)
      ))
    ).subscribe({
      next: () => {
        this.isSaving = false;
        const hasAttachments = this.selectedFiles.length > 0;
        this.dialog.open(Dialog, { data: {
            title: 'Saved',
            message: hasAttachments ? 'Draft and attachments saved successfully.' : 'Draft saved successfully.'
          }});
        this.cdr.detectChanges();
      },
      error: (err: unknown) => {
        this.isSaving = false;
        this.showError('Failed to save draft', this.getErrorMessage(err));
      }
    });
  }

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

  private buildPayload() {
    return {
      subject: this.subjectControl.value ?? '',
      content: this.bodyControl.value ?? '',
      receiver: this.toControl.value ?? [],
      carbonCopy: this.ccControl.value ?? [],
      blindCarbonCopy: this.bccControl.value ?? [],
      replyTo: this.replyToControl.value ?? []
    };
  }

  private saveMailDraft(): Observable<string> {
    const payload = this.buildPayload();
    if (this.draftId) {
      return this.mailService.updateMail(this.draftId, payload).pipe(
        tap(draft => this.updatedAt = draft.updatedAt),
        map(() => this.draftId!)
      );
    }
    return this.mailService.createMail(payload).pipe(
      tap(draft => {
        this.draftId = draft.id;
        this.updatedAt = draft.updatedAt;
      }),
      map(draft => draft.id)
    );
  }

  private uploadAttachments(mailId: string): Observable<void> {
    if (this.selectedFiles.length === 0) return of(undefined);
    return forkJoin(
      this.selectedFiles.map(file =>
        this.attachmentService.uploadAttachment(mailId, file).pipe(
          catchError((err: unknown) => throwError(() =>
            new Error(`"${file.name}": ${this.getErrorMessage(err)}`)
          ))
        )
      )
    ).pipe(map(() => undefined));
  }

  private updateFileNames(): void {
    this.selectedFileName = this.selectedFiles.map(f => f.name).join(', ');
  }

  protected readonly formatDateTime = formatDateTime;
}
