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
  
    this.startPolling();
  }

 
  private startPolling(): void {
    interval(30000) 
      .pipe(
        startWith(0), 
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


  getNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(this.apiUrl);
  }

  
  getPaginatedNotifications(page: number = 0, size: number = 20): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/paginated?page=${page}&size=${size}`);
  }

  getUnreadNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/unread`);
  }

 
  getUnreadCount(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.apiUrl}/unread/count`).pipe(
      tap(response => this.unreadCountSubject.next(response.count))
    );
  }

  markAsRead(id: number): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/${id}/read`, {}).pipe(
      tap(() => {
        // Decrease unread count
        const currentCount = this.unreadCountSubject.value;
        this.unreadCountSubject.next(Math.max(0, currentCount - 1));
      })
    );
  }

 
  markAllAsRead(): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/read-all`, {}).pipe(
      tap(() => this.unreadCountSubject.next(0))
    );
  }

  deleteNotification(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`);
  }


  deleteReadNotifications(): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/read`);
  }

 
  refreshUnreadCount(): void {
    this.getUnreadCount().subscribe();
  }
}
