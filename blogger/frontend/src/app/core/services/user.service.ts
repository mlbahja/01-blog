import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserProfile, UpdateProfile, ChangePassword } from '../models/user.model';

/**
 * UserService - Handles user profile operations
 *
 * Endpoints:
 * - GET /auth/users/me - Get current user profile
 * - GET /auth/users/{id} - Get user profile by ID
 * - PUT /auth/users/{id} - Update user profile
 * - PUT /auth/users/{id}/password - Change password
 * - DELETE /auth/users/{id} - Delete user account
 */
@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:9000/auth/users';

  constructor(private http: HttpClient) {}

  /**
   * Get current logged-in user's profile
   */
  getCurrentUserProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/me`);
  }

  /**
   * Get user profile by ID
   */
  getUserProfile(id: number): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/${id}`);
  }

  /**
   * Update user profile
   */
  updateProfile(id: number, profile: UpdateProfile): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.apiUrl}/${id}`, profile);
  }

  /**
   * Change user password
   */
  changePassword(id: number, passwords: ChangePassword): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/${id}/password`, passwords);
  }

  /**
   * Delete user account
   */
  deleteUser(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`);
  }
}
