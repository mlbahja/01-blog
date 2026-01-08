import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

interface AuthResponse {
  id: number;
  username: string;
  email: string;
  role: string;
  accessToken: string;
  refreshToken: string | null;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';
  private readonly TOKEN_KEY = 'jwt_token';
  private readonly USER_KEY = 'user_data';

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  login(credentials: { username?: string; email?: string; password: string }): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/login`, {
        email: credentials.email,
        username: credentials.username,
        password: credentials.password,
      })
      .pipe(
        tap((response: AuthResponse) => {
          
          this.setToken(response.accessToken);
          this.setUserData({
            id: response.id,
            username: response.username,
            email: response.email,
            role: response.role,
          });
          
          this.router.navigate(['/auth/home']);
        }),
      );
  }

  register(user: { username: string; email: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, user).pipe(
      tap((response: AuthResponse) => {
        
        this.setToken(response.accessToken);
        this.setUserData({
          id: response.id,
          username: response.username,
          email: response.email,
          role: response.role,
        });
        
        this.router.navigate(['/auth/home']);
      }),
    );
  }

  logout(navigate: boolean = true): void {
   
    this.removeToken();
    this.removeUserData();
    
    if (navigate) {
      this.router.navigate(['/auth/login']);
    }
  }


  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  removeToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

 
  getUserData(): any {
    const userData = localStorage.getItem(this.USER_KEY);
    return userData ? JSON.parse(userData) : null;
  }

  setUserData(data: any): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(data));
  }

  removeUserData(): void {
    localStorage.removeItem(this.USER_KEY);
  }

 
  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

  
    try {
      const payload = this.decodeToken(token);
      const isExpired = this.isTokenExpired(payload);

      if (isExpired) {
       
        this.logout(false);
        return false;
      }

      return true;
    } catch (error) {
      
      this.logout(false);
      return false;
    }
  }


  private decodeToken(token: string): any {
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload));
    } catch (error) {
      throw new Error('Invalid token format');
    }
  }


  private isTokenExpired(payload: any): boolean {
    if (!payload.exp) {
      return true;
    }
   
    const expirationDate = payload.exp * 1000;
    return Date.now() >= expirationDate;
  }

  navigateToLogin(): void {
    this.router.navigate(['/auth/login']);
  }

  navigateToHome(): void {
    this.router.navigate(['/auth/home']);
  }
}
