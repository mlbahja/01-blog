import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserProfile, UpdateProfile, ChangePassword } from '../models/user.model';


@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:8080/auth/users';

  constructor(private http: HttpClient) {}

 
  getCurrentUserProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/me`);
  }

 
  getUserProfile(id: number): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/${id}`);
  }

 
  updateProfile(id: number, profile: UpdateProfile): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.apiUrl}/${id}`, profile);
  }

 
  changePassword(id: number, passwords: ChangePassword): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/${id}/password`, passwords);
  }

  deleteUser(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`);
  }

  
  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }


  followUser(userId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${userId}/follow`, {});
  }

 
  unfollowUser(userId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${userId}/follow`);
  }


  isFollowing(userId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/${userId}/is-following`);
  }

  getFollowing(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/following`);
  }

 
  getFollowers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/followers`);
  }

  uploadProfilePicture(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/upload-profile-picture`, formData);
  }
}
