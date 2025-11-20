package com.blog.blogger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CreateCommentDTO - Data Transfer Object for creating comments
 *
 * Contains only the content field (author comes from JWT token)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDTO {
    private String content;
}
