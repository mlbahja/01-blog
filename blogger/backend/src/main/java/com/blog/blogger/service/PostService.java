package com.blog.blogger.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.blogger.models.Post;
import com.blog.blogger.models.PostLike;
import com.blog.blogger.models.User;
import com.blog.blogger.repository.PostLikeRepository;
import com.blog.blogger.repository.PostRepository;

/**
 * PostService - Handles post-related business logic
 *
 * Note: Sample data initialization has been removed because Post/Comment/Response
 * now require User relationships. Create posts through the API after registering users.
 */
@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    public Page<Post> getAllPosts(int page, int size) {
         Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return postRepository.findAll(pageable);
    }

    /**
     * Get posts only from users that the current user follows
     * If user doesn't follow anyone, returns empty list
     */
    public List<Post> getPostsFromFollowedUsers(String currentUsername) {
        // Get IDs of users that current user follows
        System.out.println("currentUsername ========> " + currentUsername);
        List<Long> followingIds = subscriptionService.getFollowingIds(currentUsername);
        if (followingIds.isEmpty()) {
            return List.of(); // Return empty list if not following anyone
        }
        // Get posts from followed users
        System.out.println("*********************************" + followingIds);
        return postRepository.findByAuthorIdInOrderByCreatedAtDesc(followingIds);
    }
    /**
     * pagination of posts that can showing by just follewed or not folwed okay 
     * If user doesn't follow anyone, returns empty list
     */
    

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Post createPost(Post post) {
        System.out.println(post);
        
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    /**
     * Like a post
     * If the user already liked the post, this does nothing
     */
    @Transactional
    public Post likePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if user already liked this post
        if (postLikeRepository.existsByUserAndPost(user, post)) {
            return post; // Already liked, do nothing
        }

        // Create new like
        PostLike like = PostLike.builder()
                .user(user)
                .post(post)
                .build();
        postLikeRepository.save(like);

        // Increment like count
        post.setLikeCount(post.getLikeCount() + 1);
        return postRepository.save(post);
    }

    /**
     * Unlike a post
     * If the user hasn't liked the post, this does nothing
     */
    @Transactional
    public Post unlikePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if user has liked this post
        Optional<PostLike> likeOpt = postLikeRepository.findByUserAndPost(user, post);
        if (likeOpt.isEmpty()) {
            return post; // Not liked, do nothing
        }

        // Delete the like
        postLikeRepository.delete(likeOpt.get());

        // Decrement like count (but don't go below 0)
        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        return postRepository.save(post);
    }

    /**
     * Check if a user has liked a specific post
     */
    public boolean hasUserLikedPost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return postLikeRepository.existsByUserAndPost(user, post);
    }
}
