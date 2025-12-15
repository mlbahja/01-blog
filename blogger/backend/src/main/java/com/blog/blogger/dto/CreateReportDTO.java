package com.blog.blogger.dto;

public class CreateReportDTO {
    
    private Long postId;      // Which post to report
    private String message;   // What's wrong with it
    
    // Getters and setters
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}