import { Routes } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { HomeComponent } from './auth/home/home.component';
import { ProfileComponent } from './auth/profile/profile.component';
import { ProfileEditComponent } from './auth/profile-edit/profile-edit.component';
import { UserSettingsComponent } from './auth/user-settings/user-settings.component';
import { AdminDashboardComponent } from './admin/admin-dashboard/admin-dashboard.component';
import { UserManagementComponent } from './admin/user-management/user-management.component';
import { NotFoundComponent } from './shared/components/not-found/not-found.component';
import { UnauthorizedComponent } from './shared/components/unauthorized/unauthorized.component';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';
import { adminGuard } from './core/guards/admin.guard';
import { AuthService } from './core/services/auth.service';

/**
 * Application Routes Configuration
 *
 * Route Protection Strategy:
 * - Public routes (login, register): Protected by guestGuard
 *   → Authenticated users are redirected to /home
 * - Protected routes (home, profile): Protected by authGuard
 *   → Unauthenticated users are redirected to /login
 * - Admin routes (admin/*): Protected by adminGuard
 *   → Non-admin users are redirected to /home
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

  // ========== Public Routes ==========
  // Only accessible when NOT logged in
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

  // ========== Protected Routes ==========
  // Only accessible when logged in
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [authGuard],
  },

  // ========== Profile Routes ==========
  // User profile management
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [authGuard],
  },
  {
    path: 'profile/edit',
    component: ProfileEditComponent,
    canActivate: [authGuard],
  },
  {
    path: 'profile/settings',
    component: UserSettingsComponent,
    canActivate: [authGuard],
  },

  // ========== Admin Routes ==========
  // Only accessible for ADMIN users
  {
    path: 'admin',
    component: AdminDashboardComponent,
    canActivate: [adminGuard],
  },
  {
    path: 'admin/users',
    component: UserManagementComponent,
    canActivate: [adminGuard],
  },

  // ========== Legacy Redirects ==========
  // Backward compatibility
  { path: 'auth/login', redirectTo: 'login', pathMatch: 'full' },
  { path: 'auth/register', redirectTo: 'register', pathMatch: 'full' },
  { path: 'auth/home', redirectTo: 'home', pathMatch: 'full' },

  // ========== Error Pages ==========
  {
    path: 'unauthorized',
    component: UnauthorizedComponent,
  },
  {
    path: 'not-found',
    component: NotFoundComponent,
  },
  // Wildcard route - MUST BE LAST
  // Catches all undefined routes and shows 404 page
  {
    path: '**',
    component: NotFoundComponent,
  },
];
