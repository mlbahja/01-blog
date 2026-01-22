import { Routes } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { HomeComponent } from './auth/home/home.component';
import { UsersComponent } from './auth/users/users.component';
import { ChatComponent } from './auth/chat/chat.component';
import { ProfileComponent } from './auth/profile/profile.component';
import { ProfileEditComponent } from './auth/profile-edit/profile-edit.component';
import { UserSettingsComponent } from './auth/user-settings/user-settings.component';
import { NotificationsComponent } from './auth/notifications/notifications.component';
import { AdminDashboardComponent } from './admin/admin-dashboard/admin-dashboard.component';
import { UserManagementComponent } from './admin/user-management/user-management.component';
import { AdminReportsComponent } from './admin/reports/reports.component';
import { NotFoundComponent } from './shared/components/not-found/not-found.component';
import { UnauthorizedComponent } from './shared/components/unauthorized/unauthorized.component';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';
import { adminGuard } from './core/guards/admin.guard';
import { AuthService } from './core/services/auth.service';
import { PostDetailComponent } from './auth/post-detail/post-detail.component';

import { AdminPostsComponent } from './admin/admin-posts/admin-posts.component';


export const routes: Routes = [

  {
    path: '',
    canActivate: [
      () => {
        const authService = inject(AuthService);
        const router = inject(Router);


        return authService.isLoggedIn()
          ? router.createUrlTree(['/home'])
          : router.createUrlTree(['/login']);
      },
    ],
    children: [],
  },
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
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [authGuard],
  },
  {
    path: 'post/:id',
    component: PostDetailComponent,
    canActivate: [authGuard],
  },
  {
    path: 'users',
    component: UsersComponent,
    canActivate: [authGuard],
  },

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
  {
    path: 'notifications',
    component: NotificationsComponent,
    canActivate: [authGuard],
  },
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
  {
    path: 'admin/reports',
    component: AdminReportsComponent,
    canActivate: [adminGuard],
  },
  {
    path: 'admin/posts',
    component: AdminPostsComponent,
    canActivate: [adminGuard], // hakda gha t7miha mn users li ma ADMIN
  },

  { path: 'auth/login', redirectTo: 'login', pathMatch: 'full' },
  { path: 'auth/register', redirectTo: 'register', pathMatch: 'full' },
  { path: 'auth/home', redirectTo: 'home', pathMatch: 'full' },
  //this is the admin 
  


  {
    path: 'unauthorized',
    component: UnauthorizedComponent,
  },
  {
    path: 'not-found',
    component: NotFoundComponent,
  },
  {
    path: '**',
    component: NotFoundComponent,
  },
];
