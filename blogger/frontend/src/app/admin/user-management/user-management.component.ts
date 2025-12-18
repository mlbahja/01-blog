import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AdminService } from '../../core/services/admin.service';
import { ToastService } from '../../core/services/toast.service';
import { UserProfile } from '../../core/models/user.model';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css'],
})
export class UserManagementComponent implements OnInit {
  users: UserProfile[] = [];
  filteredUsers: UserProfile[] = [];
  loading = true;
  searchTerm = '';
  filterRole = 'ALL';
  filterStatus = 'ALL';

  constructor(
    private adminService: AdminService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.applyFilters();
        this.loading = false;
      },
      error: () => {
        this.toastService.show('Failed to load users', 'error');
        this.loading = false;
      },
    });
  }

  applyFilters(): void {
    let filtered = [...this.users];

    // Search filter
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(
        (user) =>
          user.username.toLowerCase().includes(term) ||
          user.email.toLowerCase().includes(term) ||
          (user.fullName && user.fullName.toLowerCase().includes(term))
      );
    }

    // Role filter
    if (this.filterRole !== 'ALL') {
      filtered = filtered.filter((user) => user.role === this.filterRole);
    }

    // Status filter
    if (this.filterStatus === 'ACTIVE') {
      filtered = filtered.filter((user) => !user.isBanned);
    } else if (this.filterStatus === 'BANNED') {
      filtered = filtered.filter((user) => user.isBanned);
    }

    this.filteredUsers = filtered;
  }

  banUser(user: UserProfile): void {
    const confirmed = confirm(`Are you sure you want to ban "${user.username}"?`);
    if (confirmed) {
      this.adminService.banUser(user.id).subscribe({
        next: () => {
          this.toastService.show(`User "${user.username}" banned successfully`, 'success');
          this.loadUsers();
        },
        error: () => {
          this.toastService.show('Failed to ban user', 'error');
        },
      });
    }
  }

  unbanUser(user: UserProfile): void {
    this.adminService.unbanUser(user.id).subscribe({
      next: () => {
        this.toastService.show(`User "${user.username}" unbanned successfully`, 'success');
        this.loadUsers();
      },
      error: () => {
        this.toastService.show('Failed to unban user', 'error');
      },
    });
  }

  promoteToAdmin(user: UserProfile): void {
    const confirmed = confirm(
      `Are you sure you want to promote "${user.username}" to ADMIN?`
    );
    if (confirmed) {
      this.adminService.changeUserRole(user.id, 'ADMIN').subscribe({
        next: () => {
          this.toastService.show(`User "${user.username}" promoted to admin`, 'success');
          this.loadUsers();
        },
        error: () => {
          this.toastService.show('Failed to promote user', 'error');
        },
      });
    }
  }

  demoteToUser(user: UserProfile): void {
    const confirmed = confirm(
      `Are you sure you want to demote "${user.username}" to USER?`
    );
    if (confirmed) {
      this.adminService.changeUserRole(user.id, 'USER').subscribe({
        next: () => {
          this.toastService.show(`User "${user.username}" demoted to user`, 'success');
          this.loadUsers();
        },
        error: () => {
          this.toastService.show('Failed to demote user', 'error');
        },
      });
    }
  }

  deleteUser(user: UserProfile): void {
    const confirmed = confirm(
      `Are you sure you want to DELETE "${user.username}"? This action cannot be undone.`
    );
    if (confirmed) {
      this.adminService.deleteUser(user.id).subscribe({
        next: () => {
          this.toastService.show(`User "${user.username}" deleted successfully`, 'success');
          this.loadUsers();
        },
        error: () => {
          this.toastService.show('Failed to delete user', 'error');
        },
      });
    }
  }

  getUserAvatar(user: UserProfile): string {
    const imageUrl = user.profilePictureUrl || user.avatar;

    if (imageUrl && imageUrl.startsWith('/uploads/')) {
      return 'http://localhost:8080' + imageUrl;
    }

    return imageUrl || this.getDefaultAvatar();
  }

  getDefaultAvatar(): string {
    // Return a data URI for a simple gray circle with a user icon (40x40)
    return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9IjQwIiBoZWlnaHQ9IjQwIiBmaWxsPSIjZTBlMGUwIi8+PGNpcmNsZSBjeD0iMjAiIGN5PSIxNSIgcj0iNyIgZmlsbD0iIzk5OSIvPjxwYXRoIGQ9Ik04IDMyYzAtNyA1LjUtMTIgMTItMTJzMTIgNSAxMiAxMiIgZmlsbD0iIzk5OSIvPjwvc3ZnPg==';
  }
}
