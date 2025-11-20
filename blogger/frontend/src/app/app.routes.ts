import { Routes } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { HomeComponent } from './auth/home/home.component';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';
import { AuthService } from './core/services/auth.service';

/**
 * Application Routes Configuration
 *
 * Route Protection Strategy:
 * - Public routes (login, register): Protected by guestGuard
 *   → Authenticated users are redirected to /home
 * - Protected routes (home): Protected by authGuard
 *   → Unauthenticated users are redirected to /login
 * - Root path (''): Intelligent redirect based on auth status
 */
export const routes: Routes = [
  // Root path - Smart redirect based on authentication status
  {
    path: '',
    canActivate: [
      () => {
        const authService = inject(AuthService);
        const router = inject(Router);

        // Redirect authenticated users to home, guests to login
        return authService.isLoggedIn()
          ? router.createUrlTree(['/home'])
          : router.createUrlTree(['/login']);
      },
    ],
    children: [],
  },

  // Public routes - Only accessible when NOT logged in
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [guestGuard],
  },
  {
    path: 'register',
    component: RegisterComponent,
    canActivate: [guestGuard],
  },

  // Protected routes - Only accessible when logged in
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [authGuard],
  },

  // Legacy route redirects for backward compatibility
  { path: 'auth/login', redirectTo: 'login', pathMatch: 'full' },
  { path: 'auth/register', redirectTo: 'register', pathMatch: 'full' },
  { path: 'auth/home', redirectTo: 'home', pathMatch: 'full' },
];
