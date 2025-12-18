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
  uploading = false;
  currentUserId: number | null = null;
  selectedProfilePicture: File | null = null;

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

    // Convert empty strings to undefined to prevent saving empty values
    const updateData: UpdateProfile = {
      fullName: this.fullName?.trim() || undefined,
      bio: this.bio?.trim() || undefined,
      avatar: this.avatar?.trim() || undefined,
      profilePictureUrl: this.profilePictureUrl?.trim() || undefined,
    };

    console.log('Updating profile with:', updateData);

    this.userService.updateProfile(this.currentUserId, updateData).subscribe({
      next: (response) => {
        console.log('Profile updated:', response);
        this.toastService.show('Profile updated successfully!', 'success');
        this.loading = false;
        // Navigate with reload flag
        this.router.navigate(['/profile']).then(() => {
          window.location.reload();
        });
      },
      error: (err) => {
        console.error('Profile update error:', err);
        this.toastService.show('Failed to update profile', 'error');
        this.loading = false;
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/profile']);
  }

  onProfilePictureSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedProfilePicture = file;
    }
  }

  uploadProfilePicture(): void {
    if (this.selectedProfilePicture) {
      this.uploading = true;
      this.userService.uploadProfilePicture(this.selectedProfilePicture).subscribe({
        next: (response) => {
          this.profilePictureUrl = response.url;
          this.selectedProfilePicture = null;
          this.uploading = false;
          this.toastService.show('Profile picture uploaded successfully!', 'success');
        },
        error: (err) => {
          this.uploading = false;
          this.toastService.show('Failed to upload profile picture', 'error');
        }
      });
    }
  }

  getProfilePictureUrl(): string {
    if (this.profilePictureUrl && this.profilePictureUrl.startsWith('/uploads/')) {
      return 'http://localhost:8080' + this.profilePictureUrl;
    }
    const imageUrl = this.avatar || this.profilePictureUrl;
    return imageUrl || this.getDefaultAvatar();
  }

  getDefaultAvatar(): string {
    // Return a data URI for a simple gray circle with a user icon
    return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTUwIiBoZWlnaHQ9IjE1MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTUwIiBoZWlnaHQ9IjE1MCIgZmlsbD0iI2UwZTBlMCIvPjxjaXJjbGUgY3g9Ijc1IiBjeT0iNTUiIHI9IjI1IiBmaWxsPSIjOTk5Ii8+PHBhdGggZD0iTTMwIDEyMGMwLTI1IDIwLTQ1IDQ1LTQ1czQ1IDIwIDQ1IDQ1IiBmaWxsPSIjOTk5Ii8+PC9zdmc+';
  }
}
