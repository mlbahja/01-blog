import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { UpdateProfile } from '../../core/models/user.model';

@Component({
  selector: 'app-profile-edit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile-edit.component.html',
  styleUrls: ['./profile-edit.component.css'],
})
export class ProfileEditComponent implements OnInit {
  fullName = '';
  bio = '';
  avatar = '';
  profilePictureUrl = '';
  loading = false;
  currentUserId: number | null = null;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCurrentProfile();
  }

  loadCurrentProfile(): void {
    const userData = this.authService.getUserData();
    if (userData) {
      this.currentUserId = userData.id;
      this.userService.getCurrentUserProfile().subscribe({
        next: (profile) => {
          this.fullName = profile.fullName || '';
          this.bio = profile.bio || '';
          this.avatar = profile.avatar || '';
          this.profilePictureUrl = profile.profilePictureUrl || '';
        },
        error: () => {
          this.toastService.show('Failed to load profile', 'error');
        },
      });
    }
  }

  onSubmit(): void {
    if (!this.currentUserId) return;

    this.loading = true;

    const updateData: UpdateProfile = {
      fullName: this.fullName || undefined,
      bio: this.bio || undefined,
      avatar: this.avatar || undefined,
      profilePictureUrl: this.profilePictureUrl || undefined,
    };

    this.userService.updateProfile(this.currentUserId, updateData).subscribe({
      next: () => {
        this.toastService.show('Profile updated successfully!', 'success');
        this.loading = false;
        this.router.navigate(['/profile']);
      },
      error: (err) => {
        this.toastService.show('Failed to update profile', 'error');
        this.loading = false;
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/profile']);
  }
}
