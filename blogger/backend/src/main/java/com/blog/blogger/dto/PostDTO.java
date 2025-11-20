package com.blog.blogger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PostDTO - Data Transfer Object for blog posts
 *
 * Used for returning post information with author details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private AuthorDTO author;          // Nested DTO for author info
    private int likeCount;
    private List<String> tags;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Nested DTO for author information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthorDTO {
        private Long id;
        private String username;
        private String fullName;
        private String avatar;
    }
}
