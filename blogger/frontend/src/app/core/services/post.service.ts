import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PostService {
  private apiUrl = 'http://localhost:8080/auth'; // FIXED

  constructor(private http: HttpClient) {}

  private getAuthHeaders() {
    const token = localStorage.getItem('jwt_token');
    return {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    };
  }

  
  getAllPosts(page: number = 1, size: number = 10): Observable<any> {
    const params = new HttpParams().set('page', page).set('size', size);

    return this.http.get(`${this.apiUrl}/posts`, { params });
  }

  
  getPostsFromFollowedUsers(page: number = 1, size: number = 10): Observable<any> {
    const params = new HttpParams().set('page', page).set('size', size);

    return this.http.get(`${this.apiUrl}/posts/following`, { params });
  }


  createPost(post: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/posts`, post);
  }

 
  uploadMedia(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/posts/upload`, formData);
  }


  addComment(postId: number, comment: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/posts/${postId}/comments`, comment);
  }

  deletePost(postId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/posts/${postId}`);
  }


  updatePost(postId: number, post: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/posts/${postId}`, post);
  }


  likePost(postId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/posts/${postId}/like`, {});
  }


  unlikePost(postId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/posts/${postId}/like`);
  }

 
  hasLikedPost(postId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/posts/${postId}/liked`);
  }


  getPostById(postId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/posts/${postId}`, {
      headers: this.getAuthHeaders(),
    });
  }
}
