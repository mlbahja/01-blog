package com.blog.blogger.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import  org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blog.blogger.dto.CreateCommentDTO;
import com.blog.blogger.dto.CreatePostDTO;
import com.blog.blogger.models.Comment;
import com.blog.blogger.models.Post;
import com.blog.blogger.models.User;
import com.blog.blogger.repository.UserRepository;
import com.blog.blogger.service.CommentService;
import com.blog.blogger.service.FileStorageService;
import com.blog.blogger.service.PostService;

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

    @Autowired
    private com.blog.blogger.services.NotificationService notificationService;

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
            @AuthenticationPrincipal User currentUser) {
        List<Post> posts = postService.getPostsFromFollowedUsers(currentUser.getUsername());
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
                                           @AuthenticationPrincipal User currentUser) {
        // Check if user is banned
        checkUserBanned(currentUser);

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .mediaType(dto.getMediaType())
                .mediaUrl(dto.getMediaUrl())
                .author(currentUser)
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
            @AuthenticationPrincipal User currentUser) {

        try {
            // Check if user is banned
            checkUserBanned(currentUser);

            // Validate file
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("Please select a file to upload");
            }

            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
                throw new RuntimeException("Only image or video files are allowed");
            }

            long maxSizeBytes = 50L * 1024L * 1024L;
            if (file.getSize() > maxSizeBytes) {
                throw new RuntimeException("File size must not exceed 50MB");
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
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to upload media"));
        }
    }

    /**
     * PUT /auth/posts/{id}
     * Update an existing post (only by the author or admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id,
                                       @RequestBody CreatePostDTO dto,
                                       @AuthenticationPrincipal User currentUser) {
        try {
            // Check if user is banned
            checkUserBanned(currentUser);

            // Get the post
            Post post = postService.getPostById(id)
                    .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

            // Check if user owns the post or is admin
            boolean isOwner = post.getAuthor().getId().equals(currentUser.getId());
            boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

            //should edite just if you are the owner of the post 
            if (!isOwner && !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                            "error", "Forbidden",
                            "message", "You can only edit your own posts"
                        ));
            }

            // Update the post
            Post updatedPost = Post.builder()
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .mediaType(dto.getMediaType())
                    .mediaUrl(dto.getMediaUrl())
                    .build();

            Post savedPost = postService.updatePost(id, updatedPost);

            return ResponseEntity.ok(savedPost);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

@DeleteMapping("/{id}")
public ResponseEntity<?> deletePost(@PathVariable Long id,
                                   @AuthenticationPrincipal User currentUser) {
    try {
        // Check if user is banned
        checkUserBanned(currentUser);
        
        // Get the post
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        
        // Check if user owns the post OR is admin
        boolean isOwner = post.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        
        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                        "error", "Forbidden",
                        "message", "You can only delete your own posts"
                    ));
        }
        
        // Delete the post
        postService.deletePost(id);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Post deleted successfully"
        ));
        
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
    }
}

    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId,
                                           @RequestBody CreateCommentDTO dto,
                                           @AuthenticationPrincipal User currentUser) {
        // Check if user is banned
        checkUserBanned(currentUser);

        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .author(currentUser)
                .post(post)
                .build();

        commentService.addComment(comment);

        // Notify post author about the new comment
        notificationService.notifyUserAboutComment(post, currentUser);

        // Return a simple success response instead of the full post to avoid circular reference issues
        return ResponseEntity.ok(java.util.Map.of(
            "message", "Comment added successfully",
            "commentContent", comment.getContent(),
            "author", currentUser.getUsername()
        ));
    }

    /**
     * GET /auth/posts/{postId}/comments?page=0&size=5
     * Get paginated comments for a post
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<Map<String, Object>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        Page<Comment> commentPage = commentService.getCommentsByPost(post, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("comments", commentPage.getContent());
        response.put("total", commentPage.getTotalElements());
        response.put("totalPages", commentPage.getTotalPages());
        response.put("currentPage", page);

        return ResponseEntity.ok(response);
    }

    /**
     * POST /auth/posts/{id}/like
     * Like a post (authenticated users only)
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Post> likePost(@PathVariable Long id,
                                         @AuthenticationPrincipal User currentUser) {
        // Check if user is banned
        checkUserBanned(currentUser);

        Post post = postService.likePost(id, currentUser);
        return ResponseEntity.ok(post);
    }

    /**
     * DELETE /auth/posts/{id}/like
     * Unlike a post (authenticated users only)
     */
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Post> unlikePost(@PathVariable Long id,
                                           @AuthenticationPrincipal User currentUser) {
        // Check if user is banned
        checkUserBanned(currentUser);

        Post post = postService.unlikePost(id, currentUser);
        return ResponseEntity.ok(post);
    }

    /**
     * GET /auth/posts/{id}/liked
     * Check if the current user has liked this post
     */
    @GetMapping("/{id}/liked")
    public ResponseEntity<Boolean> hasLikedPost(@PathVariable Long id,
                                                @AuthenticationPrincipal User currentUser) {
        boolean liked = postService.hasUserLikedPost(id, currentUser);
        return ResponseEntity.ok(liked);
    }

    /**
     * POST /auth/posts/{postId}/comments/{commentId}/like
     * Like a comment (authenticated users only)
     */
    @PostMapping("/{postId}/comments/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long postId,
                                         @PathVariable Long commentId,
                                         @AuthenticationPrincipal User currentUser) {
        // Check if user is banned
        checkUserBanned(currentUser);

        Comment comment = commentService.likeComment(commentId, currentUser);
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
                                           @AuthenticationPrincipal User currentUser) {
        // Check if user is banned
        checkUserBanned(currentUser);

        Comment comment = commentService.unlikeComment(commentId, currentUser);
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
                                                    @AuthenticationPrincipal User currentUser) {
        boolean liked = commentService.hasUserLikedComment(commentId, currentUser);
        return ResponseEntity.ok(liked);
    }

    /**
 * DELETE /auth/posts/{postId}/comments/{commentId}
 * Delete a comment (only by the author, post author, or admin)
 */
@DeleteMapping("/{postId}/comments/{commentId}")
public ResponseEntity<?> deleteComment(@PathVariable Long postId,
                                       @PathVariable Long commentId,
                                       @AuthenticationPrincipal User currentUser) {
    try {
        // Check if user is banned
        checkUserBanned(currentUser);
        
        // Get the post
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        
        // Get the comment
        Comment comment = commentService.getCommentById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        
        // Verify comment belongs to the post
        if (!comment.getPost().getId().equals(post.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Comment does not belong to the specified post"));
        }
        
        // Check authorization: comment owner, post owner, or admin can delete
        boolean isCommentOwner = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isPostOwner = post.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        
        if (!isCommentOwner && !isPostOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                        "error", "Forbidden",
                        "message", "You are not authorized to delete this comment"
                    ));
        }
        
        // Delete the comment
        commentService.deleteComment(commentId);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Comment deleted successfully"
        ));
        
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error: " + e.getMessage()));
    }

    }
    
}
