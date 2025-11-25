import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { UserProfile } from '../../core/models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  userProfile: UserProfile | null = null;
  loading = true;
  currentUserId: number | null = null;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    const userData = this.authService.getUserData();
    if (userData) {
      this.currentUserId = userData.id;
      this.userService.getCurrentUserProfile().subscribe({
        next: (profile) => {
          this.userProfile = profile;
          this.loading = false;
        },
        error: (err) => {
          this.toastService.show('Failed to load profile', 'error');
          this.loading = false;
        },
      });
    }
  }

  deleteAccount(): void {
    if (!this.currentUserId) return;

    const confirmed = confirm(
      'Are you sure you want to delete your account? This action cannot be undone.'
    );

    if (confirmed) {
      this.userService.deleteUser(this.currentUserId).subscribe({
        next: () => {
          this.toastService.show('Account deleted successfully', 'success');
          this.authService.logout();
        },
        error: (err) => {
          this.toastService.show('Failed to delete account', 'error');
        },
      });
    }
  }
}
