import {
  Component,
  Input
} from '@angular/core';
import {
  ReactiveFormsModule,
  FormControl,
} from '@angular/forms';
import {MatError, MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import { NgIf} from '@angular/common';
import {MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {getErrorInputs} from '../../utils/getErrorInputs';


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

  get isPassword() {
    return this.type === 'password';
  }

  get inputType() {
    if (this.isPassword) {
      return this.hidePassword ? 'password' : 'text';
    }
    return this.type;
  }

  togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword;
  }

  getErrorMessage(): string {
    return getErrorInputs(this.control, this.label)
  }

}
