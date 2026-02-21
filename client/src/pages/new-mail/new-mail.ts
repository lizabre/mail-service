import { Component } from '@angular/core';
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {InputField} from '../../components/input-field/input-field';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf} from '@angular/common';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {ChipField} from '../../components/chip-field/chip-field';
import {emailArrayValidator} from '../../utils/emailValidator';

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
  form:FormGroup;
  showCc = false;
  showBcc = false;
  showReplyTo = false;
  selectedFileName: string = '';

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      to: [[''], [Validators.required, emailArrayValidator()]],
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
      console.log(input.files);
      this.attachmentsControl.setValue(Array.from(input.files));
      this.selectedFileName = Array.from(input.files).map(f => f.name).join(', ');
    }
  }

  triggerFileInput(): void {
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    fileInput.click();
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      console.log("fail", this.form.value);
      return;
    }

    const mailData = this.form.value;
      console.log("success", mailData);
  }
}
