package com.blog.blogger.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Message Entity - Represents a chat message between two users
 *
 * sender: The user who sent the message
 * receiver: The user who receives the message
 * content: The message text
 * read: Whether the message has been read by the receiver
 */
@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_sender_receiver", columnList = "sender_id,receiver_id"),
    @Index(name = "idx_receiver_read", columnList = "receiver_id,is_read")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"sender", "receiver"})
@EqualsAndHashCode(exclude = {"sender", "receiver"})
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean read = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
