import {ChangeDetectionStrategy, Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';

/**
 * Reusable dialog component for displaying informational messages and errors.
 */
@Component({
  selector: 'app-dialog',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogClose
  ],
  templateUrl: './dialog.html',
  styleUrl: './dialog.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Dialog {
  constructor(
    public dialogRef: MatDialogRef<Dialog>,
    /** Injected data containing the dialog title and message. */
    @Inject(MAT_DIALOG_DATA) public data: { title: string; message: string; }
  ) {}
}
