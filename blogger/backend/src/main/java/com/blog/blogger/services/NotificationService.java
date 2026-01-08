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
     * 
     * 
     * @param post 
     * @param followers
     */
    @Transactional
    public void notifyFollowersAboutNewPost(Post post, List<User> followers) {
        User author = post.getAuthor();

        for (User follower : followers) {
            
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

   
    @Transactional
    public void notifyUserAboutNewFollower(User followedUser, User follower) {
        
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

   
    @Transactional
    public void notifyUserAboutPostLike(Post post, User liker) {
        User postAuthor = post.getAuthor();

        if (postAuthor.getId().equals(liker.getId())) {
            return;
        }

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

  
    @Transactional
    public void notifyUserAboutComment(Post post, User commenter) {
        User postAuthor = post.getAuthor();

      
        if (postAuthor.getId().equals(commenter.getId())) {
            return;
        }

        
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

  
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

   
    public Page<Notification> getUserNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

   
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

  
    public Long getUnreadNotificationCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

   
    @Transactional
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to mark this notification as read");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    
    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadForUser(user);
    }

   
    @Transactional
    public void deleteNotification(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this notification");
        }

        notificationRepository.delete(notification);
    }

   
    @Transactional
    public void deleteReadNotifications(User user) {
        notificationRepository.deleteReadNotificationsForUser(user);
    }

   
    @Transactional
    public void deleteAllNotificationsForUser(User user) {
        notificationRepository.deleteByUser(user);
    }
}
