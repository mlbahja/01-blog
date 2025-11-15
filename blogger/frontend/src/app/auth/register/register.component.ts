import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  confirmPassword = '';

  constructor(
    private authService: AuthService,
    private toastService: ToastService
  ) {}

  onSubmit() {
    // Client-side validation
    if (!this.username || !this.email || !this.password || !this.confirmPassword) {
      this.toastService.show('❌ All fields are required', 'error');
      return;
    }

    if (this.username.length < 3) {
      this.toastService.show('❌ Username must be at least 3 characters', 'error');
      return;
    }

    if (this.password.length < 6) {
      this.toastService.show('❌ Password must be at least 6 characters', 'error');
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.toastService.show('❌ Passwords do not match', 'error');
      return;
    }

    const user = {
      username: this.username,
      email: this.email,
      password: this.password,
    };

    this.authService.register(user).subscribe({
      next: (res: any) => {
        this.toastService.show('✅ Registration successful! Welcome ' + res.username, 'success');
        // Note: AuthService.register() now auto-navigates to home
      },
      error: (err: any) => {
        // Handle different error types
        if (err.status === 400 && err.error?.errors) {
          // Validation errors from backend
          const errors = err.error.errors;
          const errorMessages = Object.values(errors).join(', ');
          this.toastService.show('❌ ' + errorMessages, 'error');
        } else if (err.status === 409) {
          // Conflict - email/username already in use
          this.toastService.show('❌ ' + err.error, 'error');
        } else if (err.error?.message) {
          this.toastService.show('❌ ' + err.error.message, 'error');
        } else {
          this.toastService.show('❌ Registration failed. Please try again', 'error');
        }
      },
    });
  }
}
