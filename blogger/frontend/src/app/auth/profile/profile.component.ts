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
          console.log('Profile loaded:', profile);
          console.log('Avatar:', profile.avatar);
          console.log('ProfilePictureUrl:', profile.profilePictureUrl);
          this.userProfile = profile;
          this.loading = false;
        },
        error: (err) => {
          console.error('Failed to load profile:', err);
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

  getProfilePictureUrl(): string {
    if (!this.userProfile) {
      return this.getDefaultAvatar();
    }

    // Prioritize profilePictureUrl (uploaded image) over avatar (URL)
    const imageUrl = this.userProfile.profilePictureUrl || this.userProfile.avatar;
    console.log("=====>" + imageUrl);
    
    if (imageUrl && imageUrl.startsWith('/uploads/')) {
      return 'http://localhost:8080' + imageUrl;
    }

    return imageUrl || this.getDefaultAvatar();
  }

  getDefaultAvatar(): string {
    // Return a data URI for a simple gray circle with a user icon
    return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTUwIiBoZWlnaHQ9IjE1MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTUwIiBoZWlnaHQ9IjE1MCIgZmlsbD0iI2UwZTBlMCIvPjxjaXJjbGUgY3g9Ijc1IiBjeT0iNTUiIHI9IjI1IiBmaWxsPSIjOTk5Ii8+PHBhdGggZD0iTTMwIDEyMGMwLTI1IDIwLTQ1IDQ1LTQ1czQ1IDIwIDQ1IDQ1IiBmaWxsPSIjOTk5Ii8+PC9zdmc+';
  }
}
