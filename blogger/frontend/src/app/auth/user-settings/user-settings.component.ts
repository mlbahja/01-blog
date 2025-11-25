import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { ChangePassword } from '../../core/models/user.model';

@Component({
  selector: 'app-user-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.css'],
})
export class UserSettingsComponent {
  currentPassword = '';
  newPassword = '';
  confirmPassword = '';
  loading = false;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {}

  onSubmit(): void {
    // Validation
    if (!this.currentPassword || !this.newPassword || !this.confirmPassword) {
      this.toastService.show('All fields are required', 'error');
      return;
    }

    if (this.newPassword.length < 6) {
      this.toastService.show('New password must be at least 6 characters', 'error');
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.toastService.show('Passwords do not match', 'error');
      return;
    }

    const userData = this.authService.getUserData();
    if (!userData) {
      this.toastService.show('User not found', 'error');
      return;
    }

    this.loading = true;

    const changePasswordData: ChangePassword = {
      currentPassword: this.currentPassword,
      newPassword: this.newPassword,
      confirmPassword: this.confirmPassword,
    };

    this.userService.changePassword(userData.id, changePasswordData).subscribe({
      next: () => {
        this.toastService.show('Password changed successfully!', 'success');
        this.loading = false;
        this.resetForm();
        this.router.navigate(['/profile']);
      },
      error: (err) => {
        const errorMsg = err.error?.error || 'Failed to change password';
        this.toastService.show(errorMsg, 'error');
        this.loading = false;
      },
    });
  }

  resetForm(): void {
    this.currentPassword = '';
    this.newPassword = '';
    this.confirmPassword = '';
  }

  cancel(): void {
    this.router.navigate(['/profile']);
  }
}
