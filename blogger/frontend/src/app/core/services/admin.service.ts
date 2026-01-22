// import { Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable } from 'rxjs';
// import { AdminStats, ChangeRoleRequest } from '../models/admin.model';
// import { UserProfile } from '../models/user.model';
// import { Post } from '../models/post.model';
// import { map } from 'rxjs/operators';

// @Injectable({
//   providedIn: 'root',
// })
// export class AdminService {
//   private apiUrl = 'http://localhost:8080/auth/admin';

//   constructor(private http: HttpClient) { }
//   hidePost(id: number): Observable<{ message: string }> {
//     return this.http.put<{ message: string }>(`${this.apiUrl}/posts/${id}/hide`, {});
//   }

//   unhidePost(id: number): Observable<{ message: string }> {
//     return this.http.put<{ message: string }>(`${this.apiUrl}/posts/${id}/unhide`, {});
//   }

//   getDashboardStats(): Observable<AdminStats> {
//     return this.http.get<AdminStats>(`${this.apiUrl}/stats`);
//   }


//   getAllUsers(): Observable<UserProfile[]> {
//     return this.http.get<UserProfile[]>(`${this.apiUrl}/users`);
//   }


//   banUser(id: number): Observable<{ message: string }> {
//     return this.http.put<{ message: string }>(`${this.apiUrl}/users/${id}/ban`, {});
//   }


//   unbanUser(id: number): Observable<{ message: string }> {
//     return this.http.put<{ message: string }>(`${this.apiUrl}/users/${id}/unban`, {});
//   }


//   changeUserRole(id: number, role: 'USER' | 'ADMIN'): Observable<{ message: string }> {
//     return this.http.put<{ message: string }>(`${this.apiUrl}/users/${id}/role`, { role });
//   }


//   deleteUser(id: number): Observable<{ message: string }> {
//     return this.http.delete<{ message: string }>(`${this.apiUrl}/users/${id}`);
//   }


//   // getAllPosts(): Observable<Post[]> {
//   //   return this.http.get<Post[]>(`${this.apiUrl}/posts`);
//   // }
//   getAllPosts(): Observable<Post[]> {
//     return this.http.get<{ posts: Post[] }>(`${this.apiUrl}/posts`).pipe(
//       map(res => res.posts)  // hna ghanextractiw array mn object
//     );
//   }


//   deletePost(id: number): Observable<{ message: string }> {
//     return this.http.delete<{ message: string }>(`${this.apiUrl}/posts/${id}`);
//   }
// }


import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AdminStats } from '../models/admin.model';
import { UserProfile } from '../models/user.model';
import { Post } from '../models/post.model';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private apiUrl = 'http://localhost:8080/auth/admin';

  constructor(private http: HttpClient) {}

  // helper bach njibo headers m token
  private getAuthHeaders(): { headers: HttpHeaders } {
    const token = localStorage.getItem('accessToken'); // token mn login
    return {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + token,
      }),
    };
  }

  hidePost(id: number): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(
      `${this.apiUrl}/posts/${id}/hide`,
      {},
      this.getAuthHeaders()
    );
  }

  unhidePost(id: number): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(
      `${this.apiUrl}/posts/${id}/unhide`,
      {},
      this.getAuthHeaders()
    );
  }

  getAllPosts(): Observable<Post[]> {
    return this.http
      .get<{ posts: Post[] }>(`${this.apiUrl}/posts`, this.getAuthHeaders())
      .pipe(map((res) => res.posts));
  }

  deletePost(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(
      `${this.apiUrl}/posts/${id}`,
      this.getAuthHeaders()
    );
  }

  // similarly add headers to other admin requests
  getDashboardStats(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.apiUrl}/stats`, this.getAuthHeaders());
  }

  getAllUsers(): Observable<UserProfile[]> {
    return this.http.get<UserProfile[]>(`${this.apiUrl}/users`, this.getAuthHeaders());
  }

  banUser(id: number): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/users/${id}/ban`, {}, this.getAuthHeaders());
  }

  unbanUser(id: number): Observable<{ message: string }>{
    return this.http.put<{ message: string }>(`${this.apiUrl}/users/${id}/unban`, {}, this.getAuthHeaders());
  }

  changeUserRole(id: number, role: 'USER' | 'ADMIN'): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/users/${id}/role`, { role }, this.getAuthHeaders());
  }

  deleteUser(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/users/${id}`, this.getAuthHeaders());
  }
}
