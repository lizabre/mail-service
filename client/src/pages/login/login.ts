import {Component} from '@angular/core';
import {InputField} from "../../components/input-field/input-field";
import {MatButton} from "@angular/material/button";
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthService} from '../../api/auth-service/auth.service';
import {Router} from '@angular/router';
import {passwordStrengthValidator} from '../../utils/passwordStrendthValidator';
import {DialogService} from '../../utils/dialogService';

@Component({
  selector: 'app-login',
  imports: [
    InputField,
    MatButton,
    ReactiveFormsModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  form: FormGroup;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router, private dialogService: DialogService) {
    console.log(authService.isLoggedIn())
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8), passwordStrengthValidator()]],
    });
  }

  get emailControl() {
    return this.form.get('email') as FormControl;
  }

  get passwordControl() {
    return this.form.get('password') as FormControl;
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const {...loginData} = this.form.value;
    this.authService.login(loginData).subscribe({
      next: () => this.router.navigate(['/']),
      error: (err) => {this.dialogService.info({title: 'Login Failed', message: err.error?.errors || 'An error occurred during login. Please try again.'}); console.log(err)}
    });
  }

}
