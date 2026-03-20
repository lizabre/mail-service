import { Component } from '@angular/core';
import {InputField} from '../../components/input-field/input-field';
import {MatButton} from '@angular/material/button';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {passwordMatchValidator} from '../../utils/passwordMatchValidator';
import {MatError} from '@angular/material/input';
import {NgIf} from '@angular/common';
import {AuthService} from '../../api/auth-service/auth.service';
import {Router} from '@angular/router';
import {passwordStrengthValidator} from '../../utils/passwordStrendthValidator';
import {DialogService} from '../../utils/dialogService';

/**
 * Page component for user registration.
 * Validates all fields including password strength and match,
 * then registers the user and navigates to home on success.
 */
@Component({
  selector: 'app-registration',
  imports: [InputField, MatButton, ReactiveFormsModule, MatError, NgIf],
  templateUrl: './registration.html',
  styleUrl: './registration.css',
})
export class Registration {
  form: FormGroup;

  constructor(private fb: FormBuilder, private authService:AuthService, private router: Router, private dialogService:DialogService) {
    this.form = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(3)]],
      lastName: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8), passwordStrengthValidator()]],
      confirmPassword: ['', [Validators.required, Validators.minLength(8), passwordStrengthValidator()]],
    }, { validators: passwordMatchValidator() });
  }

  get firstNameControl() { return this.form.get('firstName') as FormControl; }
  get lastNameControl() { return this.form.get('lastName') as FormControl; }
  get emailControl() { return this.form.get('email') as FormControl; }
  get passwordControl() { return this.form.get('password') as FormControl; }
  get confirmPasswordControl() { return this.form.get('confirmPassword') as FormControl; }

  /**
   * Submits the registration form.
   * Navigates to home on success or shows an error dialog on failure.
   */
  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { confirmPassword, ...registerData } = this.form.value;

    this.authService.register(registerData).subscribe({
      next: () => this.router.navigate(['/']),
      error: (err) => this.dialogService.info({title: 'Registration Failed', message: err.error?.errors || 'An error occurred during registration. Please try again.'})
    });
  }
}
