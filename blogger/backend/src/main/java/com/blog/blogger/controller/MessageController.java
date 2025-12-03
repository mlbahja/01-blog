package com.blog.blogger.controller;

import com.blog.blogger.models.Message;
import com.blog.blogger.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/messages")
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * Send a message to another user
     * POST /auth/messages
     * Body: { "receiverId": 123, "content": "Hello!" }
     */
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> payload, Principal principal) {
        try {
            Long receiverId = Long.valueOf(payload.get("receiverId").toString());
            String content = payload.get("content").toString();

            Message message = messageService.sendMessage(principal.getName(), receiverId, content);

            Map<String, Object> response = new HashMap<>();
            response.put("id", message.getId());
            response.put("content", message.getContent());
            response.put("createdAt", message.getCreatedAt());
            response.put("message", "Message sent successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get conversation with another user
     * GET /auth/messages/conversation/{userId}
     */
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<?> getConversation(@PathVariable Long userId, Principal principal) {
        try {
            List<Map<String, Object>> messages = messageService.getConversation(principal.getName(), userId);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all conversations for current user
     * GET /auth/messages/conversations
     */
    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations(Principal principal) {
        try {
            List<Map<String, Object>> conversations = messageService.getConversations(principal.getName());
            return ResponseEntity.ok(conversations);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Mark messages as read
     * PUT /auth/messages/read/{userId}
     */
    @PutMapping("/read/{userId}")
    public ResponseEntity<?> markAsRead(@PathVariable Long userId, Principal principal) {
        try {
            messageService.markMessagesAsRead(principal.getName(), userId);
            return ResponseEntity.ok(Map.of("message", "Messages marked as read"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get unread message count
     * GET /auth/messages/unread-count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Principal principal) {
        long count = messageService.getUnreadCount(principal.getName());
        return ResponseEntity.ok(count);
    }

    /**
     * Delete a message
     * DELETE /auth/messages/{messageId}
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId, Principal principal) {
        try {
            messageService.deleteMessage(principal.getName(), messageId);
            return ResponseEntity.ok(Map.of("message", "Message deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
