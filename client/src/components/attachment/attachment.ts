import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-attachment',
  imports: [
    MatButton,
    MatIcon,
    MatIconButton
  ],
  templateUrl: './attachment.html',
  styleUrl: './attachment.css',
})
export class Attachment {
  @Input({required: true}) fileName!: string;
  @Input({required: true}) mimeType!: string;
  @Input({required: true}) size!: number;
  @Input({required: true}) attachmentId!: string;
  @Input() url: string| null = null;

  @Output() deleteClicked = new EventEmitter<string>();
  @Output() open = new EventEmitter<void>();

  get formattedSize(): string {
    if (this.size < 1024) return `${this.size} B`;
    if (this.size < 1024 * 1024) return `${(this.size / 1024).toFixed(1)} KB`;
    return `${(this.size / (1024 * 1024)).toFixed(1)} MB`;
  }

  onDelete(): void {
    this.deleteClicked.emit(this.attachmentId);
  }

  onOpen(): void {
    if (!this.url) { this.open.emit(); return; }

    const previewable = ['application/pdf', 'image/png', 'image/jpeg', 'image/gif'];
    if (previewable.includes(this.mimeType)) {
      window.open(this.url, '_blank');
    } else {
      const a = document.createElement('a');
      a.href = this.url;
      a.download = this.fileName;
      a.click();
    }
  }
}
