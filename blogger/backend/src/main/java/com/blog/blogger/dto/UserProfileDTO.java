package com.blog.blogger.dto;

import com.blog.blogger.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UserProfileDTO - Data Transfer Object for user profile
 *
 * Used for returning user profile information without exposing sensitive data
 * (password is excluded)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String bio;
    private String avatar;
    private String profilePictureUrl;
    private Role role;
    private Boolean isBanned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer postCount;      // Optional: number of posts by user
    private Integer commentCount;   // Optional: number of comments by user
}
