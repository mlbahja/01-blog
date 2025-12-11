import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PostService {
  private apiUrl = 'http://localhost:8080/auth'; // FIXED

  constructor(private http: HttpClient) {}

  // GET paginated posts
  getAllPosts(page: number = 1, size: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get(`${this.apiUrl}/posts`, { params });
  }

  // GET posts from followed users
  getPostsFromFollowedUsers(page: number = 1, size: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get(`${this.apiUrl}/posts/following`, { params });
  }

  // CREATE post
  createPost(post: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/posts`, post);
  }

  // UPLOAD media file
  uploadMedia(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/posts/upload`, formData);
  }

  // ADD comment
  addComment(postId: number, comment: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/posts/${postId}/comments`, comment);
  }

  // DELETE post
  deletePost(postId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/posts/${postId}`);
  }

  // LIKE
  likePost(postId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/posts/${postId}/like`, {});
  }

  // UNLIKE
  unlikePost(postId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/posts/${postId}/like`);
  }

  // CHECK IF USER LIKED A POST
  hasLikedPost(postId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/posts/${postId}/liked`);
  }
}
