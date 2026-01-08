import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';


export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

 
  if (!authService.isLoggedIn()) {
    console.log('Admin Guard: User not logged in, redirecting to login');
    router.navigate(['/login']);
    return false;
  }

 
  const userData = authService.getUserData();
  if (userData && userData.role === 'ADMIN') {
    return true; 
  }

  
  console.log('Admin Guard: User is not admin, redirecting to unauthorized');
  router.navigate(['/unauthorized']);
  return false; 
};
