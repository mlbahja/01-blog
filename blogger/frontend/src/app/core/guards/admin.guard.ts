import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Admin Guard - Protects admin routes from non-admin users
 *
 * This guard:
 * 1. Checks if user is logged in and is an ADMIN
 * 2. If yes, allows access to admin routes
 * 3. If no, redirects to home page
 * 4. Works by checking user role from localStorage
 */
export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // First check if user is logged in
  if (!authService.isLoggedIn()) {
    console.log('Admin Guard: User not logged in, redirecting to login');
    router.navigate(['/login']);
    return false;
  }

  // Check if user is admin
  const userData = authService.getUserData();
  if (userData && userData.role === 'ADMIN') {
    return true; // Allow access to admin routes
  }

  // User is not admin, redirect to unauthorized page
  console.log('Admin Guard: User is not admin, redirecting to unauthorized');
  router.navigate(['/unauthorized']);
  return false; // Block access to admin routes
};
