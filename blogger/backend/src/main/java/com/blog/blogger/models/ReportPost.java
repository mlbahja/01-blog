package com.blog.blogger.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.Data;

@Entity
@Table(name = "reports")
/* 
@NoArgsConstructor
@AllArgsConstructor
*/
@Data
public class ReportPost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Who reported
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;
    
    // Which post was reported
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    // The report message
    @Column(columnDefinition = "TEXT")
    private String message;
    
    // Status: just pending/resolved
    @Column(nullable = false)
    private boolean resolved = false;
    
    // When was it created
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Admin can add notes when resolving
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;
    
    // --- Constructors ---
    //public Report() {}
    
    // Simple constructor
    /*public Report(User reporter, Post post, String message) {
        this.reporter = reporter;
        this.post = post;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }*/
    
    // --- Getters and Setters ---

}