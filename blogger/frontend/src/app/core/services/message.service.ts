import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MessageService {
  private apiUrl = 'http://localhost:8080/auth/messages';

  constructor(private http: HttpClient) {}

 
  sendMessage(receiverId: number, content: string): Observable<any> {
    return this.http.post(this.apiUrl, { receiverId, content });
  }

  
  getConversation(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/conversation/${userId}`);
  }

  
  getConversations(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/conversations`);
  }

  
  markAsRead(userId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/read/${userId}`, {});
  }

  
  getUnreadCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/unread-count`);
  }

  
  deleteMessage(messageId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${messageId}`);
  }
}
