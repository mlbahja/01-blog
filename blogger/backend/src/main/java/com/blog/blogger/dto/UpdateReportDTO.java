package com.blog.blogger.dto;

public class UpdateReportDTO {
    private boolean resolved;     // true/false
    private String adminNotes;    // Optional notes
    
    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
    
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
}