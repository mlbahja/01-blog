import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { RouterModule } from '@angular/router';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  username = '';
  password = '';

  constructor(
    private authService: AuthService,
    private toastService: ToastService
  ) {}

  onSubmit() {
    // Client-side validation
    if (!this.username || !this.password) {
      this.toastService.show('❌ Username and password are required', 'error');
      return;
    }

    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: (res: any) => {
        this.toastService.show('✅ Welcome back, ' + res.username + '!', 'success');
        // Note: AuthService.login() now auto-navigates to home
      },
      error: (err: any) => {
        // Handle different error types
        if (err.status === 401) {
          this.toastService.show('❌ Invalid username or password', 'error');
        } else if (err.status === 400 && err.error?.errors) {
          // Validation errors from backend
          const errors = err.error.errors;
          const errorMessages = Object.values(errors).join(', ');
          this.toastService.show('❌ ' + errorMessages, 'error');
        } else if (err.error?.message) {
          this.toastService.show('❌ ' + err.error.message, 'error');
        } else {
          this.toastService.show('❌ Login failed. Please try again', 'error');
        }
      },
    });
  }
}
