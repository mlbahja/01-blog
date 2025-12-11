package com.blog.blogger.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import  org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.blogger.dto.CreateCommentDTO;
import com.blog.blogger.dto.CreatePostDTO;
import com.blog.blogger.models.Comment;
import com.blog.blogger.models.Post;
import com.blog.blogger.models.User;
import com.blog.blogger.repository.UserRepository;
import com.blog.blogger.service.CommentService;
import com.blog.blogger.service.PostService;
import com.blog.blogger.service.FileStorageService;
//pagination of Posts
import org.springframework.data.domain.Page;
import java.util.Map;
import java.util.HashMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/auth/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Check if user is banned and throw exception if so
     */
    private void checkUserBanned(User user) {
        if (user.getIsBanned() != null && user.getIsBanned()) {
            throw new RuntimeException("User account is banned and cannot perform this action");
        }
    }
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getAllPosts(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
) {
    Page<Post> postPage = postService.getAllPosts(page, size);

    Map<String, Object> response = new HashMap<>();
    response.put("posts", postPage.getContent());
    response.put("total", postPage.getTotalElements());
    response.put("totalPages", postPage.getTotalPages());
    response.put("currentPage", page);

    return ResponseEntity.ok(response);
}

    @GetMapping("/following")
    public ResponseEntity<List<Post>> getPostsFromFollowedUsers(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<Post> posts = postService.getPostsFromFollowedUsers(userDetails.getUsername());
        return ResponseEntity.ok(posts);
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

        // Check if user is banned
        checkUserBanned(author);

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .mediaType(dto.getMediaType())
                .mediaUrl(dto.getMediaUrl())
                .author(author)
                .build();

        Post savedPost = postService.createPost(post);
        return ResponseEntity.ok(savedPost);
    }

    /**
     * POST /auth/posts/upload
     * Upload a media file for a post
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is banned
        checkUserBanned(user);

        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("Please select a file to upload");
        }

        // Store file
        String filename = fileStorageService.storeFile(file);
        String mediaType = fileStorageService.determineMediaType(file);

        // Build the URL
        String fileUrl = "/uploads/" + filename;

        Map<String, String> response = new HashMap<>();
        response.put("filename", filename);
        response.put("url", fileUrl);
        response.put("mediaType", mediaType);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId,
                                           @RequestBody CreateCommentDTO dto,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        User author = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is banned
        checkUserBanned(author);

        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .author(author)
                .post(post)
                .build();

        post.addComment(comment);
        postService.createPost(post);

        // Return a simple success response instead of the full post to avoid circular reference issues
        return ResponseEntity.ok(java.util.Map.of(
            "message", "Comment added successfully",
            "commentContent", comment.getContent(),
            "author", author.getUsername()
        ));
    }

    /**
     * POST /auth/posts/{id}/like
     * Like a post (authenticated users only)
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Post> likePost(@PathVariable Long id,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is banned
        checkUserBanned(user);

        Post post = postService.likePost(id, user);
        return ResponseEntity.ok(post);
    }

    /**
     * DELETE /auth/posts/{id}/like
     * Unlike a post (authenticated users only)
     */
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Post> unlikePost(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is banned
        checkUserBanned(user);

        Post post = postService.unlikePost(id, user);
        return ResponseEntity.ok(post);
    }

    /**
     * GET /auth/posts/{id}/liked
     * Check if the current user has liked this post
     */
    @GetMapping("/{id}/liked")
    public ResponseEntity<Boolean> hasLikedPost(@PathVariable Long id,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean liked = postService.hasUserLikedPost(id, user);
        return ResponseEntity.ok(liked);
    }

    /**
     * POST /auth/posts/{postId}/comments/{commentId}/like
     * Like a comment (authenticated users only)
     */
    @PostMapping("/{postId}/comments/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long postId,
                                         @PathVariable Long commentId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is banned
        checkUserBanned(user);

        Comment comment = commentService.likeComment(commentId, user);
        return ResponseEntity.ok(java.util.Map.of(
            "message", "Comment liked",
            "likeCount", comment.getLikeCount()
        ));
    }

    /**
     * DELETE /auth/posts/{postId}/comments/{commentId}/like
     * Unlike a comment (authenticated users only)
     */
    @DeleteMapping("/{postId}/comments/{commentId}/like")
    public ResponseEntity<?> unlikeComment(@PathVariable Long postId,
                                           @PathVariable Long commentId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is banned
        checkUserBanned(user);

        Comment comment = commentService.unlikeComment(commentId, user);
        return ResponseEntity.ok(java.util.Map.of(
            "message", "Comment unliked",
            "likeCount", comment.getLikeCount()
        ));
    }

    /**
     * GET /auth/posts/{postId}/comments/{commentId}/liked
     * Check if the current user has liked this comment
     */
    @GetMapping("/{postId}/comments/{commentId}/liked")
    public ResponseEntity<Boolean> hasLikedComment(@PathVariable Long postId,
                                                    @PathVariable Long commentId,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean liked = commentService.hasUserLikedComment(commentId, user);
        return ResponseEntity.ok(liked);
    }
}
