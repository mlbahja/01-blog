
export enum Role {
  USER = 'USER',
  ADMIN = 'ADMIN',
}

export interface User {
  id: number;
  username: string;
  email: string;
  fullName?: string;
  bio?: string;
  avatar?: string;
  profilePictureUrl?: string;
  role: Role;
  isBanned: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UserProfile {
  id: number;
  username: string;
  email: string;
  fullName?: string;
  bio?: string;
  avatar?: string;
  profilePictureUrl?: string;
  role: Role;
  isBanned: boolean;
  createdAt: string;
  updatedAt: string;
  postCount?: number;
  commentCount?: number;
}

export interface UpdateProfile {
  fullName?: string;
  bio?: string;
  avatar?: string;
  profilePictureUrl?: string;
}

export interface ChangePassword {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface AuthResponse {
  id: number;
  username: string;
  email: string;
  role: Role;
  accessToken: string;
  refreshToken: string | null;
}
