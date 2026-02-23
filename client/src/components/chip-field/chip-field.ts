import {Component, Input, signal} from '@angular/core';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatIcon} from '@angular/material/icon';
import {NgIf} from '@angular/common';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {MatChipGrid, MatChipInput, MatChipInputEvent, MatChipRow} from '@angular/material/chips';
import {getErrorInputs} from '../../utils/getErrorInputs';

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

  private isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  removeKeyword(keyword: string) {
    this.chipList.update(chipList => {
      const index = chipList.indexOf(keyword);
      if (index < 0) {
        return chipList;
      }

      chipList.splice(index, 1);
      return [...chipList];
    });
  }

  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    if (value) {
      if (this.isValidEmail(value)) {
        this.chipList.update(chips => [...chips, value]);
      }
    }
    event.chipInput!.clear();
  }
  getErrorMessage(): string {
    return getErrorInputs(this.control, this.label)
  }
}
