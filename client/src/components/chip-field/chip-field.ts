import {Component, Input, signal} from '@angular/core';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatIcon} from '@angular/material/icon';
import {NgIf} from '@angular/common';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {MatChipGrid, MatChipInput, MatChipInputEvent, MatChipRow} from '@angular/material/chips';
import {getErrorInputs} from '../../utils/getErrorInputs';

/**
 * Reusable chip input field for entering and managing a list of email addresses.
 * Only valid email addresses are added as chips.
 */
@Component({
  selector: 'app-chip-field',
  imports: [
    MatError,
    MatFormField,
    MatIcon,
    MatInput,
    MatLabel,
    NgIf,
    ReactiveFormsModule,
    MatChipGrid,
    MatChipRow,
    MatChipInput
  ],
  templateUrl: './chip-field.html',
  styleUrl: './chip-field.css',
})
export class ChipField {
  @Input({ required: true }) label!: string;
  @Input({ required: true }) placeholder!: string;
  @Input() control: FormControl = new FormControl();

  readonly chipList = signal<string[]>([]);

  /**
   * Adds a new email chip if the input value is a valid email.
   * Clears the input after adding.
   * @param event The chip input event.
   */
  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    if (value && this.isValidEmail(value)) {
      this.chipList.update(chips => [...chips, value]);
    }
    event.chipInput!.clear();
  }

  /**
   * Removes a chip from the list by value.
   * @param keyword The email string to remove.
   */
  removeKeyword(keyword: string): void {
    this.chipList.update(chips => {
      const index = chips.indexOf(keyword);
      if (index < 0) return chips;
      chips.splice(index, 1);
      return [...chips];
    });
  }

  /**
   * Returns a validation error message for the current control state.
   * @returns The error message string.
   */
  getErrorMessage(): string {
    return getErrorInputs(this.control, this.label);
  }

  private isValidEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }
}
