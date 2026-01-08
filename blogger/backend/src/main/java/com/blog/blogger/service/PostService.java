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

    public Page<Post> getAllPosts(int page, int size) {
         Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return postRepository.findByIsHiddenFalseOrIsHiddenIsNull(pageable);
    }

    
    public Page<Post> getAllPostsIncludingHidden(int page, int size) {
         Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return postRepository.findAll(pageable);
    }

    
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

    
    @Transactional
    public Post updatePost(Long id, Post updatedPost) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setMediaType(updatedPost.getMediaType());
        existingPost.setMediaUrl(updatedPost.getMediaUrl());

        return postRepository.save(existingPost);
    }

    
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

    
    public boolean hasUserLikedPost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return postLikeRepository.existsByUserAndPost(user, post);
    }

    
    @Transactional
    public Post hidePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        post.setIsHidden(true);
        return postRepository.save(post);
    }

    
    @Transactional
    public Post unhidePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        post.setIsHidden(false);
        return postRepository.save(post);
    }
}
