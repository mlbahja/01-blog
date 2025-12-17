package com.blog.blogger.repositories;

import com.blog.blogger.models.Notification;
import com.blog.blogger.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find all notifications for a user, ordered by most recent first
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // Find all notifications for a user with pagination
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // Find unread notifications for a user
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

    // Count unread notifications for a user
    Long countByUserAndIsReadFalse(User user);

    // Mark all notifications as read for a user
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user AND n.isRead = false")
    void markAllAsReadForUser(@Param("user") User user);

    // Delete all notifications for a user (useful when deleting user account)
    void deleteByUser(User user);

    // Delete old read notifications (for cleanup tasks)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.user = :user AND n.isRead = true")
    void deleteReadNotificationsForUser(@Param("user") User user);
}
