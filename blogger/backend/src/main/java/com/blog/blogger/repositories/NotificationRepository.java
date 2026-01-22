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

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

    Long countByUserAndIsReadFalse(User user);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user AND n.isRead = false")
    void markAllAsReadForUser(@Param("user") User user);

    void deleteByUser(User user);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.user = :user AND n.isRead = true")
    void deleteReadNotificationsForUser(@Param("user") User user);
}



//  // Find notifications by user
//     List<Notification> findByUser(User user);
    
//     // Delete notifications by user
//     @Modifying
//     @Query("DELETE FROM Notification n WHERE n.user = :user")
//     void deleteByUser(@Param("user") User user);