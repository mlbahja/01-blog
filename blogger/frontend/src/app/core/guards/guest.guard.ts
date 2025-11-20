import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Guest Guard - Protects login/register routes from authenticated users
 *
 * This guard:
 * 1. Checks if user is already logged in (has valid JWT token)
 * 2. If yes, redirects to home page (no need to login again)
 * 3. If no, allows access to login/register pages
 * 4. Prevents authenticated users from accessing public auth pages
 *
 * Use case: Apply to login and register routes
 */
export const guestGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Check if user is already logged in
  if (authService.isLoggedIn()) {
    // User is authenticated, redirect to home
    console.log('Guest Guard: User already logged in, redirecting to home');
    router.navigate(['/home']);
    return false; // Block access to login/register
  }

  // User is not logged in, allow access to login/register
  return true;
};
