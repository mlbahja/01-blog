import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, interval } from 'rxjs';
import { tap, switchMap, startWith } from 'rxjs/operators';
import { Notification } from '../models/notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = 'http://localhost:8080/auth/notifications';
  
  private unreadCountSubject = new BehaviorSubject<number>(0);
  public unreadCount$ = this.unreadCountSubject.asObservable();

  constructor(private http: HttpClient) {
    // Poll for unread count every 30 seconds
    this.startPolling();
  }

  /**
   * Start polling for unread notifications count
   */
  private startPolling(): void {
    interval(30000) // Poll every 30 seconds
      .pipe(
        startWith(0), // Start immediately
        switchMap(() => this.getUnreadCount())
      )
      .subscribe({
        next: (response) => {
          this.unreadCountSubject.next(response.count);
        },
        error: (err) => {
          console.error('Error fetching unread count:', err);
        }
      });
  }

  /**
   * Get all notifications
   */
  getNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(this.apiUrl);
  }

  /**
   * Get paginated notifications
   */
  getPaginatedNotifications(page: number = 0, size: number = 20): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/paginated?page=${page}&size=${size}`);
  }

  /**
   * Get unread notifications only
   */
  getUnreadNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/unread`);
  }

  /**
   * Get unread notifications count
   */
  getUnreadCount(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.apiUrl}/unread/count`).pipe(
      tap(response => this.unreadCountSubject.next(response.count))
    );
  }

  /**
   * Mark a specific notification as read
   */
  markAsRead(id: number): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/${id}/read`, {}).pipe(
      tap(() => {
        // Decrease unread count
        const currentCount = this.unreadCountSubject.value;
        this.unreadCountSubject.next(Math.max(0, currentCount - 1));
      })
    );
  }

  /**
   * Mark all notifications as read
   */
  markAllAsRead(): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/read-all`, {}).pipe(
      tap(() => this.unreadCountSubject.next(0))
    );
  }

  /**
   * Delete a specific notification
   */
  deleteNotification(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`);
  }

  /**
   * Delete all read notifications
   */
  deleteReadNotifications(): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/read`);
  }

  /**
   * Refresh unread count manually
   */
  refreshUnreadCount(): void {
    this.getUnreadCount().subscribe();
  }
}
