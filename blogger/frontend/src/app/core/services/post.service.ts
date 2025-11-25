import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PostService {
  private apiUrl = 'http://localhost:8080/auth/posts'; // Backend API URL

  constructor(private http: HttpClient) {}

  getAllPosts(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  createPost(post: any): Observable<any> {
    return this.http.post(this.apiUrl, post);
  }

  addComment(postId: number, comment: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/${postId}/comments`, comment);
  }

  deletePost(postId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${postId}`);
  }

  likePost(postId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${postId}/like`, {});
  }

  unlikePost(postId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${postId}/like`);
  }

  hasLikedPost(postId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/${postId}/liked`);
  }
}
