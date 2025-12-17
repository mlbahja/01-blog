export interface Notification {
  id: number;
  user: {
    id: number;
    username: string;
  };
  message: string;
  type: NotificationType;
  relatedPostId?: number;
  relatedUserId?: number;
  isRead: boolean;
  createdAt: string;
}

export enum NotificationType {
  NEW_POST = 'NEW_POST',
  NEW_FOLLOWER = 'NEW_FOLLOWER',
  POST_LIKE = 'POST_LIKE',
  COMMENT = 'COMMENT',
  COMMENT_LIKE = 'COMMENT_LIKE'
}
