import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-attachment',
  imports: [
    MatButton
  ],
  templateUrl: './attachment.html',
  styleUrl: './attachment.css',
})
export class Attachment {
  @Input({required: true}) fileName!: string;
  @Input({required: true}) mimeType!: string;
  @Input({required: true}) size!: number;
  @Input({required: true}) attachmentId!: string;

  @Output() deleteClicked = new EventEmitter<string>();

  get formattedSize(): string {
    if (this.size < 1024) return `${this.size} B`;
    if (this.size < 1024 * 1024) return `${(this.size / 1024).toFixed(1)} KB`;
    return `${(this.size / (1024 * 1024)).toFixed(1)} MB`;
  }

  onDelete(): void {
    this.deleteClicked.emit(this.attachmentId);
  }
}
