import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminStats, ChangeRoleRequest } from '../models/admin.model';
import { UserProfile } from '../models/user.model';
import { Post } from '../models/post.model';

/**
 * AdminService - Handles admin-only operations
 *
 * Endpoints:
 * - GET /auth/admin/stats - Dashboard statistics
 * - GET /auth/admin/users - Get all users
 * - PUT /auth/admin/users/{id}/ban - Ban a user
 * - PUT /auth/admin/users/{id}/unban - Unban a user
 * - PUT /auth/admin/users/{id}/role - Change user role
 * - DELETE /auth/admin/users/{id} - Delete a user
 * - GET /auth/admin/posts - Get all posts
 * - DELETE /auth/admin/posts/{id} - Delete a post
 */
@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private apiUrl = 'http://localhost:8080/auth/admin';

  constructor(private http: HttpClient) {}

  /**
   * Get dashboard statistics
   */
  getDashboardStats(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.apiUrl}/stats`);
  }

  /**
   * Get all users
   */
  getAllUsers(): Observable<UserProfile[]> {
    return this.http.get<UserProfile[]>(`${this.apiUrl}/users`);
  }

  /**
   * Ban a user
   */
  banUser(id: number): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/users/${id}/ban`, {});
  }

  /**
   * Unban a user
   */
  unbanUser(id: number): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/users/${id}/unban`, {});
  }

  /**
   * Change user role (promote/demote)
   */
  changeUserRole(id: number, role: 'USER' | 'ADMIN'): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/users/${id}/role`, { role });
  }

  /**
   * Delete a user
   */
  deleteUser(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/users/${id}`);
  }

  /**
   * Get all posts (for moderation)
   */
  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/posts`);
  }

  /**
   * Delete a post
   */
  deletePost(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/posts/${id}`);
  }
}
