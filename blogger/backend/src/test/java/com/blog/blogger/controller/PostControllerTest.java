package com.blog.blogger.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.blog.blogger.dto.CreatePostDTO;
import com.blog.blogger.models.Post;
import com.blog.blogger.models.Role;
import com.blog.blogger.models.User;
import com.blog.blogger.service.PostService;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @Test
    void updatePostRejectsNonOwner() {
        User author = User.builder()
                .id(1L)
                .role(Role.USER)
                .build();
        Post post = Post.builder()
                .id(10L)
                .author(author)
                .build();
        User currentUser = User.builder()
                .id(2L)
                .role(Role.USER)
                .build();

        when(postService.getPostById(10L)).thenReturn(Optional.of(post));

        CreatePostDTO dto = new CreatePostDTO("title", "content", null, null, null);
        ResponseEntity<?> response = postController.updatePost(10L, dto, currentUser);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(postService, never()).updatePost(anyLong(), any(Post.class));
    }
}
