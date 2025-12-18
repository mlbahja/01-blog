package com.blog.blogger.controllers;

import com.blog.blogger.models.Notification;
import com.blog.blogger.models.User;
import com.blog.blogger.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/auth/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Get all notifications for the current user
     * GET /auth/notifications
     */
    @GetMapping
    public ResponseEntity<?> getUserNotifications(
            @AuthenticationPrincipal User currentUser) {
        try {
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            List<Notification> notifications = notificationService.getUserNotifications(currentUser);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get paginated notifications for the current user
     * GET /auth/notifications/paginated?page=0&size=10
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<Notification>> getUserNotificationsPaginated(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationService.getUserNotifications(currentUser, pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notifications for the current user
     * GET /auth/notifications/unread
     */
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @AuthenticationPrincipal User currentUser) {
        List<Notification> notifications = notificationService.getUnreadNotifications(currentUser);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get count of unread notifications
     * GET /auth/notifications/unread/count
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadNotificationCount(
            @AuthenticationPrincipal User currentUser) {
        Long count = notificationService.getUnreadNotificationCount(currentUser);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark a specific notification as read
     * PUT /auth/notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        try {
            notificationService.markAsRead(id, currentUser);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification marked as read");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Mark all notifications as read
     * PUT /auth/notifications/read-all
     */
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(
            @AuthenticationPrincipal User currentUser) {
        notificationService.markAllAsRead(currentUser);
        Map<String, String> response = new HashMap<>();
        response.put("message", "All notifications marked as read");
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a specific notification
     * DELETE /auth/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNotification(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        try {
            notificationService.deleteNotification(id, currentUser);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete all read notifications (cleanup)
     * DELETE /auth/notifications/read
     */
    @DeleteMapping("/read")
    public ResponseEntity<Map<String, String>> deleteReadNotifications(
            @AuthenticationPrincipal User currentUser) {
        notificationService.deleteReadNotifications(currentUser);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Read notifications deleted successfully");
        return ResponseEntity.ok(response);
    }
}
