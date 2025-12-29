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

    @Autowired
    private com.blog.blogger.services.NotificationService notificationService;

    /**
     * Get all posts (excluding hidden posts for regular users)
     */
    public Page<Post> getAllPosts(int page, int size) {
         Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return postRepository.findByIsHiddenFalseOrIsHiddenIsNull(pageable);
    }

    /**
     * Get all posts including hidden (admin only)
     */
    public Page<Post> getAllPostsIncludingHidden(int page, int size) {
         Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return postRepository.findAll(pageable);
    }

    /**
     * Get posts only from users that the current user follows
     * If user doesn't follow anyone, returns empty list
     */
    public List<Post> getPostsFromFollowedUsers(String currentUsername) {
        List<Long> followingIds = subscriptionService.getFollowingIds(currentUsername);
        if (followingIds.isEmpty()) {
            return List.of();
        }
        return postRepository.findNonHiddenPostsByAuthorIds(followingIds);
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public Post createPost(Post post) {
        Post savedPost = postRepository.save(post);

        List<User> followers = subscriptionService.getFollowers(savedPost.getAuthor());
        notificationService.notifyFollowersAboutNewPost(savedPost, followers);

        return savedPost;
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    /**
     * Update an existing post
     * Returns the updated post or throws exception if not found
     */
    @Transactional
    public Post updatePost(Long id, Post updatedPost) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        // Update fields
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setMediaType(updatedPost.getMediaType());
        existingPost.setMediaUrl(updatedPost.getMediaUrl());

        return postRepository.save(existingPost);
    }

    /**
     * Like a post
     * If the user already liked the post, this does nothing
     */
    @Transactional
    public Post likePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (postLikeRepository.existsByUserAndPost(user, post)) {
            return post;
        }

        PostLike like = PostLike.builder()
                .user(user)
                .post(post)
                .build();
        postLikeRepository.save(like);

        post.setLikeCount(post.getLikeCount() + 1);
        Post savedPost = postRepository.save(post);

        notificationService.notifyUserAboutPostLike(savedPost, user);

        return savedPost;
    }

    /**
     * Unlike a post
     * If the user hasn't liked the post, this does nothing
     */
    @Transactional
    public Post unlikePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<PostLike> likeOpt = postLikeRepository.findByUserAndPost(user, post);
        if (likeOpt.isEmpty()) {
            return post;
        }

        postLikeRepository.delete(likeOpt.get());
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

    /**
     * Hide a post (admin only - soft delete)
     */
    @Transactional
    public Post hidePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        post.setIsHidden(true);
        return postRepository.save(post);
    }

    /**
     * Unhide a post (admin only)
     */
    @Transactional
    public Post unhidePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        post.setIsHidden(false);
        return postRepository.save(post);
    }
}
