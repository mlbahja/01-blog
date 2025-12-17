package com.blog.blogger.services;

import com.blog.blogger.models.Notification;
import com.blog.blogger.models.Post;
import com.blog.blogger.models.User;
import com.blog.blogger.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Create a notification when a user publishes a new post
     * This will notify all followers of the author
     * @param post The post that was created
     * @param followers List of followers to notify (passed from SubscriptionService to avoid circular dependency)
     */
    @Transactional
    public void notifyFollowersAboutNewPost(Post post, List<User> followers) {
        User author = post.getAuthor();

        for (User follower : followers) {
            // Don't notify banned users
            if (follower.getIsBanned()) {
                continue;
            }

            Notification notification = new Notification();
            notification.setUser(follower);
            notification.setMessage(author.getUsername() + " published a new post: " + post.getTitle());
            notification.setType(Notification.NotificationType.NEW_POST);
            notification.setRelatedPostId(post.getId());
            notification.setRelatedUserId(author.getId());

            notificationRepository.save(notification);
        }
    }

    /**
     * Create a notification when someone follows a user
     */
    @Transactional
    public void notifyUserAboutNewFollower(User followedUser, User follower) {
        // Don't notify if user is banned
        if (followedUser.getIsBanned()) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(followedUser);
        notification.setMessage(follower.getUsername() + " started following you");
        notification.setType(Notification.NotificationType.NEW_FOLLOWER);
        notification.setRelatedUserId(follower.getId());

        notificationRepository.save(notification);
    }

    /**
     * Create a notification when someone likes a post
     */
    @Transactional
    public void notifyUserAboutPostLike(Post post, User liker) {
        User postAuthor = post.getAuthor();

        // Don't notify if user likes their own post
        if (postAuthor.getId().equals(liker.getId())) {
            return;
        }

        // Don't notify if post author is banned
        if (postAuthor.getIsBanned()) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(postAuthor);
        notification.setMessage(liker.getUsername() + " liked your post: " + post.getTitle());
        notification.setType(Notification.NotificationType.POST_LIKE);
        notification.setRelatedPostId(post.getId());
        notification.setRelatedUserId(liker.getId());

        notificationRepository.save(notification);
    }

    /**
     * Create a notification when someone comments on a post
     */
    @Transactional
    public void notifyUserAboutComment(Post post, User commenter) {
        User postAuthor = post.getAuthor();

        // Don't notify if user comments on their own post
        if (postAuthor.getId().equals(commenter.getId())) {
            return;
        }

        // Don't notify if post author is banned
        if (postAuthor.getIsBanned()) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(postAuthor);
        notification.setMessage(commenter.getUsername() + " commented on your post: " + post.getTitle());
        notification.setType(Notification.NotificationType.COMMENT);
        notification.setRelatedPostId(post.getId());
        notification.setRelatedUserId(commenter.getId());

        notificationRepository.save(notification);
    }

    /**
     * Get all notifications for a user
     */
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Get paginated notifications for a user
     */
    public Page<Notification> getUserNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    /**
     * Get unread notifications for a user
     */
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    /**
     * Get count of unread notifications for a user
     */
    public Long getUnreadNotificationCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    /**
     * Mark a specific notification as read
     */
    @Transactional
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // Ensure user can only mark their own notifications as read
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to mark this notification as read");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Mark all notifications as read for a user
     */
    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadForUser(user);
    }

    /**
     * Delete a notification
     */
    @Transactional
    public void deleteNotification(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // Ensure user can only delete their own notifications
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this notification");
        }

        notificationRepository.delete(notification);
    }

    /**
     * Delete all read notifications for a user (cleanup)
     */
    @Transactional
    public void deleteReadNotifications(User user) {
        notificationRepository.deleteReadNotificationsForUser(user);
    }

    /**
     * Delete all notifications for a user (used when deleting account)
     */
    @Transactional
    public void deleteAllNotificationsForUser(User user) {
        notificationRepository.deleteByUser(user);
    }
}
