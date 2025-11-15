import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * JWT Interceptor - Automatically adds JWT token to HTTP requests
 *
 * This interceptor:
 * 1. Checks if a JWT token exists in localStorage
 * 2. If found, adds it to the Authorization header as "Bearer <token>"
 * 3. Skips adding token for login/register requests (they don't need it)
 */
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  // Skip adding token for login and register requests
  const isAuthRequest = req.url.includes('/auth/login') || req.url.includes('/auth/register');

  // If we have a token and it's not a login/register request, add it to the header
  if (token && !isAuthRequest) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(req);
};
