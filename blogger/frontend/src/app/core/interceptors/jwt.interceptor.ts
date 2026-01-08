import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { catchError, throwError } from 'rxjs';


export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();

  
  const isLoginRequest = req.url.endsWith('/auth/login');
  const isRegisterRequest = req.url.endsWith('/auth/register');
  const skipToken = isLoginRequest || isRegisterRequest;

 
  if (req.method === 'POST' && !skipToken) {
    console.log('[JWT Interceptor] POST to:', req.url);
    console.log('[JWT Interceptor] Token present:', !!token);
    if (token) {
      console.log('[JWT Interceptor] Token preview:', token.substring(0, 50) + '...');
    }
  }

 
  if (token && !skipToken) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
     
      if (error.status === 403 && error.error?.banned === true) {
        console.error('[JWT Interceptor] User account is banned. Logging out...');
        
        authService.logout();
        
        router.navigate(['/login']);
      }
     
      return throwError(() => error);
    })
  );
};
