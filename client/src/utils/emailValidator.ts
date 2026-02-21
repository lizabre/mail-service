import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function emailArrayValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;

    if (!value || !Array.isArray(value) || value.length === 0) {
      return null;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    const invalidEmails = value.filter((email: string) => !emailRegex.test(email));

    return invalidEmails.length > 0 ? { invalidEmails: { value: invalidEmails } } : null;
  };
}

