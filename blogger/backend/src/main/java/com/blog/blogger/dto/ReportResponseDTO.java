package com.blog.blogger.dto;

import com.blog.blogger.models.Report;
import java.time.LocalDateTime;

public class ReportResponseDTO {
    private Long id;
    private Long postId;
    private String postTitle;
    private Long reporterId;
    private String reporterUsername;
    private String message;
    private boolean resolved;
    private LocalDateTime createdAt;
    private String adminNotes;
    
    public ReportResponseDTO(Report report) {
        this.id = report.getId();
        this.postId = report.getPost().getId();
        this.postTitle = report.getPost().getTitle();
        this.reporterId = report.getReporter().getId();
        this.reporterUsername = report.getReporter().getUsername();
        this.message = report.getMessage();
        this.resolved = report.isResolved();
        this.createdAt = report.getCreatedAt();
        this.adminNotes = report.getAdminNotes();
    }
    
    public Long getId() { return id; }
    public Long getPostId() { return postId; }
    public String getPostTitle() { return postTitle; }
    public Long getReporterId() { return reporterId; }
    public String getReporterUsername() { return reporterUsername; }
    public String getMessage() { return message; }
    public boolean isResolved() { return resolved; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getAdminNotes() { return adminNotes; }
}