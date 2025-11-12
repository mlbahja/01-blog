import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:9000/auth';

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  login(credentials: { username?: string; email?: string; password: string }): Observable<any> {
    return this.http
      .post(`${this.apiUrl}/login`, {
        email: credentials.email,
        username: credentials.username,
        password: credentials.password,
      })
      .pipe(
        tap((response: any) => {
          // Store user info in localStorage
          localStorage.setItem('username', credentials.username || credentials.email || '');
          localStorage.setItem('isLoggedIn', 'true');
          // Route to home page after successful login
          this.router.navigate(['/auth/home']);
        }),
      );
  }

  register(user: { username: string; email: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, user).pipe(
      tap(() => {
        // Route to login page after successful registration
        this.router.navigate(['/auth/login']);
      }),
    );
  }

  logout(): void {
    // Clear user data from localStorage
    localStorage.removeItem('username');
    localStorage.removeItem('isLoggedIn');
    // Route to login page
    this.router.navigate(['/auth/login']);
  }

  isLoggedIn(): boolean {
    return localStorage.getItem('isLoggedIn') === 'true';
  }

  navigateToLogin(): void {
    this.router.navigate(['/auth/login']);
  }

  navigateToHome(): void {
    this.router.navigate(['/auth/home']);
  }
}
