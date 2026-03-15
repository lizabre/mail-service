import {Component, Input} from '@angular/core';
import {ReactiveFormsModule, FormControl} from '@angular/forms';
import {MatError, MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import { NgIf} from '@angular/common';
import {MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {getErrorInputs} from '../../utils/getErrorInputs';

/**
 * Reusable input field component with label, validation errors and password toggle.
 */
@Component({
  selector: 'app-input-field',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatError,
    NgIf,
    MatInput,
    MatIconButton,
    MatIcon,
    MatSuffix,
  ],
  templateUrl: './input-field.html'
})
export class InputField {
  @Input({ required: true }) label!: string;
  @Input({ required: true }) placeholder!: string;
  @Input() type = 'text';
  @Input() control: FormControl = new FormControl();
  hidePassword = true;

  /** Returns true if the input type is password. */
  get isPassword() {
    return this.type === 'password';
  }

  /** Returns the effective input type, toggling between 'password' and 'text'. */
  get inputType() {
    return this.isPassword ? (this.hidePassword ? 'password' : 'text') : this.type;
  }

  /** Toggles password visibility. */
  togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword;
  }

  /**
   * Returns a human-readable validation error message for the current control state.
   * @returns The error message string.
   */
  getErrorMessage(): string {
    return getErrorInputs(this.control, this.label)
  }
}
