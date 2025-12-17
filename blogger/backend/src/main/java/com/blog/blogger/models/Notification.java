package com.blog.blogger.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The user who receives this notification

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(name = "related_post_id")
    private Long relatedPostId; // Post ID for post-related notifications

    @Column(name = "related_user_id")
    private Long relatedUserId; // User ID for follow notifications

    @Column(nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isRead == null) {
            isRead = false;
        }
    }

    public enum NotificationType {
        NEW_POST,           // Someone you follow published a new post
        NEW_FOLLOWER,       // Someone followed you
        POST_LIKE,          // Someone liked your post
        COMMENT,            // Someone commented on your post
        COMMENT_LIKE        // Someone liked your comment
    }
}
