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

  return next(req);
};
