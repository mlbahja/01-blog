import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Auth Guard - Protects routes from unauthorized access
 *
 * This guard:
 * 1. Checks if user has a valid JWT token
 * 2. If yes, allows access to the route
 * 3. If no, redirects to login page
 * 4. Works even when using browser back/forward buttons
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Check if user is logged in (has JWT token)
  if (authService.isLoggedIn()) {
    return true; // Allow access to the route
  }

  // User is not logged in, redirect to login
  console.log('Auth Guard: User not logged in, redirecting to login');
  router.navigate(['/auth/login']);
  return false; // Block access to the route
};
