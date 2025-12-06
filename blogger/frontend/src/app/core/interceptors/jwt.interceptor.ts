import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { catchError, throwError } from 'rxjs';

/**
 * JWT Interceptor - Automatically adds JWT token to HTTP requests and handles banned users
 *
 * This interceptor:
 * 1. Checks if a JWT token exists in localStorage
 * 2. If found, adds it to the Authorization header as "Bearer <token>"
 * 3. Skips adding token for login/register requests (they don't need it)
 * 4. Handles 403 errors for banned users and automatically logs them out
 */
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();

  // Skip adding token ONLY for login and register endpoints
  const isLoginRequest = req.url.endsWith('/auth/login');
  const isRegisterRequest = req.url.endsWith('/auth/register');
  const skipToken = isLoginRequest || isRegisterRequest;

  // Debug logging - only for POST requests to help troubleshoot
  if (req.method === 'POST' && !skipToken) {
    console.log('[JWT Interceptor] POST to:', req.url);
    console.log('[JWT Interceptor] Token present:', !!token);
    if (token) {
      console.log('[JWT Interceptor] Token preview:', token.substring(0, 50) + '...');
    }
  }

  // If we have a token and it's not a login/register request, add it to the header
  if (token && !skipToken) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  // Handle the response and catch errors
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // Check if this is a banned user error (403 with banned flag)
      if (error.status === 403 && error.error?.banned === true) {
        console.error('[JWT Interceptor] User account is banned. Logging out...');
        // Automatically logout the user
        authService.logout();
        // Redirect to login page
        router.navigate(['/login']);
      }
      // Re-throw the error so components can handle it too
      return throwError(() => error);
    })
  );
};
