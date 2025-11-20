package com.blog.blogger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UpdateProfileDTO - Data Transfer Object for updating user profile
 *
 * Contains only the fields that users can update
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDTO {
    private String fullName;
    private String bio;
    private String avatar;
    private String profilePictureUrl;
}
