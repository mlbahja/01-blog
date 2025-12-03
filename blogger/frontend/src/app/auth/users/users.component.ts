import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
})
export class UsersComponent implements OnInit {
  users: any[] = [];
  loading = true;

  constructor(
    private userService: UserService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getAllUsers().subscribe({
      next: (users: any) => {
        this.users = users;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading users:', error);
        this.toastService.show('Failed to load users', 'error');
        this.loading = false;
      },
    });
  }

  toggleFollow(user: any): void {
    if (user.isFollowing) {
      // Unfollow
      this.userService.unfollowUser(user.id).subscribe({
        next: () => {
          user.isFollowing = false;
          user.followersCount = (user.followersCount || 1) - 1;
          this.toastService.show(`Unfollowed ${user.username}`, 'success');
        },
        error: (error: any) => {
          console.error('Error unfollowing user:', error);
          this.toastService.show('Failed to unfollow user', 'error');
        },
      });
    } else {
      // Follow
      this.userService.followUser(user.id).subscribe({
        next: () => {
          user.isFollowing = true;
          user.followersCount = (user.followersCount || 0) + 1;
          this.toastService.show(`Now following ${user.username}`, 'success');
        },
        error: (error: any) => {
          console.error('Error following user:', error);
          this.toastService.show('Failed to follow user', 'error');
        },
      });
    }
  }
}
