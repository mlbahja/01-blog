package com.blog.blogger.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.blog.blogger.dto.CreateCommentDTO;
import com.blog.blogger.dto.CreatePostDTO;
import com.blog.blogger.models.Comment;
import com.blog.blogger.models.Post;
import com.blog.blogger.models.User;
import com.blog.blogger.repository.UserRepository;
import com.blog.blogger.service.PostService;

@RestController
@RequestMapping("/auth/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody CreatePostDTO dto,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        User author = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(author)
                .build();

        Post savedPost = postService.createPost(post);
        return ResponseEntity.ok(savedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Post> addComment(@PathVariable Long postId,
                                           @RequestBody CreateCommentDTO dto,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        User author = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .author(author)
                .post(post)
                .build();

        post.addComment(comment);
        Post savedPost = postService.createPost(post);
        return ResponseEntity.ok(savedPost);
    }
}
