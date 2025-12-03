import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MessageService {
  private apiUrl = 'http://localhost:8080/auth/messages';

  constructor(private http: HttpClient) {}

  /**
   * Send a message to another user
   */
  sendMessage(receiverId: number, content: string): Observable<any> {
    return this.http.post(this.apiUrl, { receiverId, content });
  }

  /**
   * Get conversation with another user
   */
  getConversation(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/conversation/${userId}`);
  }

  /**
   * Get all conversations for current user
   */
  getConversations(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/conversations`);
  }

  /**
   * Mark messages as read
   */
  markAsRead(userId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/read/${userId}`, {});
  }

  /**
   * Get unread message count
   */
  getUnreadCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/unread-count`);
  }

  /**
   * Delete a message
   */
  deleteMessage(messageId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${messageId}`);
  }
}
