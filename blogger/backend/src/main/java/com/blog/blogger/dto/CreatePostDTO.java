package com.blog.blogger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CreatePostDTO - Data Transfer Object for creating new posts
 *
 * Contains only the fields needed to create a post
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDTO {
    private String title;
    private String content;
    private List<String> tags;
}
