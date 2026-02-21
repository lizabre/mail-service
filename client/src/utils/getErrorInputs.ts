import {AbstractControl} from '@angular/forms';

export function getErrorInputs(control: AbstractControl, label: string): string {
  if (control.hasError('required')) return `${label} is required`;
  if (control.hasError('minlength')) {
    const min = control.getError('minlength').requiredLength;
    return `Minimum ${min} characters required`;
  }
  if (control.hasError('email')) return 'Enter a valid email address';

  const passwordErrors = [];
  if (control.hasError('noUppercase')) passwordErrors.push('uppercase letter');
  if (control.hasError('noLowercase')) passwordErrors.push('lowercase letter');
  if (control.hasError('noDigit')) passwordErrors.push('digit');
  if (control.hasError('noSpecialChar')) passwordErrors.push('special character');

  if (passwordErrors.length > 0) {
    return `Password must contain at least one: ${passwordErrors.join(', ')}`;
  }

  return 'Invalid value';
}
