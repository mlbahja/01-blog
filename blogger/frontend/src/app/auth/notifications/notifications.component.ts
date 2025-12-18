import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { NotificationService } from '../../core/services/notification.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { Notification } from '../../core/models/notification.model';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit {
  notifications: Notification[] = [];
  loading = false;
  showUnreadOnly = false;
  username: string = '';

  constructor(
    private notificationService: NotificationService,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const userData = this.authService.getUserData();
    this.username = userData?.username || 'Guest';
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.loading = true;
    const observable = this.showUnreadOnly
      ? this.notificationService.getUnreadNotifications()
      : this.notificationService.getNotifications();

    observable.subscribe({
      next: (data) => {
        this.notifications = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading notifications:', err);
        this.toastService.show('Failed to load notifications', 'error');
        this.loading = false;
      }
    });
  }

  toggleFilter(): void {
    this.showUnreadOnly = !this.showUnreadOnly;
    this.loadNotifications();
  }

  markAsRead(notification: Notification): void {
    if (notification.isRead) {
      this.navigateToRelated(notification);
      return;
    }

    this.notificationService.markAsRead(notification.id).subscribe({
      next: () => {
        notification.isRead = true;
        this.notificationService.refreshUnreadCount();
        this.navigateToRelated(notification);
      },
      error: (err) => {
        console.error('Error marking notification as read:', err);
        this.toastService.show('Failed to mark as read', 'error');
      }
    });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.notifications.forEach(n => n.isRead = true);
        this.toastService.show('All notifications marked as read', 'success');
        this.notificationService.refreshUnreadCount();
      },
      error: (err) => {
        console.error('Error marking all as read:', err);
        this.toastService.show('Failed to mark all as read', 'error');
      }
    });
  }

  deleteNotification(notification: Notification, event: Event): void {
    event.stopPropagation();

    if (!confirm('Are you sure you want to delete this notification?')) {
      return;
    }

    this.notificationService.deleteNotification(notification.id).subscribe({
      next: () => {
        this.notifications = this.notifications.filter(n => n.id !== notification.id);
        this.toastService.show('Notification deleted', 'success');
        if (!notification.isRead) {
          this.notificationService.refreshUnreadCount();
        }
      },
      error: (err) => {
        console.error('Error deleting notification:', err);
        this.toastService.show('Failed to delete notification', 'error');
      }
    });
  }

  deleteReadNotifications(): void {
    if (!confirm('Delete all read notifications?')) {
      return;
    }

    this.notificationService.deleteReadNotifications().subscribe({
      next: () => {
        this.notifications = this.notifications.filter(n => !n.isRead);
        this.toastService.show('Read notifications deleted', 'success');
      },
      error: (err) => {
        console.error('Error deleting read notifications:', err);
        this.toastService.show('Failed to delete notifications', 'error');
      }
    });
  }

  navigateToRelated(notification: Notification): void {
    if (notification.relatedPostId) {
      this.router.navigate(['/post', notification.relatedPostId]);
    } else if (notification.relatedUserId) {
      this.router.navigate(['/profile'], {
        queryParams: { userId: notification.relatedUserId }
      });
    }
  }

  getNotificationIcon(type: string): string {
    switch (type) {
      case 'NEW_POST': return '📝';
      case 'NEW_FOLLOWER': return '👤';
      case 'POST_LIKE': return '❤️';
      case 'COMMENT': return '💬';
      case 'COMMENT_LIKE': return '👍';
      default: return '🔔';
    }
  }

  getTimeAgo(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);

    if (seconds < 60) return 'Just now';
    if (seconds < 3600) return `${Math.floor(seconds / 60)}m ago`;
    if (seconds < 86400) return `${Math.floor(seconds / 3600)}h ago`;
    if (seconds < 604800) return `${Math.floor(seconds / 86400)}d ago`;
    return date.toLocaleDateString();
  }

  logout(): void {
    this.authService.logout(true);
  }

  isAdmin(): boolean {
    const userData = this.authService.getUserData();
    return userData?.role === 'ADMIN';
  }
}
