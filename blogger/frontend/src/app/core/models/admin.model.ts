

export interface AdminStats {
  totalUsers: number;
  totalPosts: number;
  totalComments: number;
  activeUsers: number;
  bannedUsers: number;
  adminUsers: number;
  postsToday: number;
  commentsToday: number;
  newUsersThisWeek: number;
}

export interface ChangeRoleRequest {
  role: 'USER' | 'ADMIN';
}
