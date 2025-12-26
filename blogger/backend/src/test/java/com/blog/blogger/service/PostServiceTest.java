package com.blog.blogger.service;

import com.blog.blogger.models.Post;
import com.blog.blogger.models.PostLike;
import com.blog.blogger.models.Role;
import com.blog.blogger.models.User;
import com.blog.blogger.repository.PostLikeRepository;
import com.blog.blogger.repository.PostRepository;
import com.blog.blogger.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PostService
 * Tests core business logic for post operations
 */
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PostService postService;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("hashedPassword")
                .role(Role.USER)
                .build();

        // Create test post
        testPost = Post.builder()
                .id(1L)
                .title("Test Post")
                .content("Test Content")
                .author(testUser)
                .likeCount(0)
                .isHidden(false)
                .build();
    }

    @Test
    void getAllPosts_ShouldReturnNonHiddenPostsOnly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> posts = Arrays.asList(testPost);
        Page<Post> postPage = new PageImpl<>(posts, pageable, 1);

        when(postRepository.findByIsHiddenFalseOrIsHiddenIsNull(any(Pageable.class)))
                .thenReturn(postPage);

        // Act
        Page<Post> result = postService.getAllPosts(1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testPost.getTitle(), result.getContent().get(0).getTitle());
        verify(postRepository).findByIsHiddenFalseOrIsHiddenIsNull(any(Pageable.class));
    }

    @Test
    void getPostById_ShouldReturnPost_WhenPostExists() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // Act
        Optional<Post> result = postService.getPostById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testPost.getTitle(), result.get().getTitle());
        verify(postRepository).findById(1L);
    }

    @Test
    void getPostById_ShouldReturnEmpty_WhenPostDoesNotExist() {
        // Arrange
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Post> result = postService.getPostById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(postRepository).findById(999L);
    }

    @Test
    void createPost_ShouldSavePostAndNotifyFollowers() {
        // Arrange
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(subscriptionService.getFollowers(any(User.class))).thenReturn(Arrays.asList());

        // Act
        Post result = postService.createPost(testPost);

        // Assert
        assertNotNull(result);
        assertEquals(testPost.getTitle(), result.getTitle());
        verify(postRepository).save(testPost);
        verify(subscriptionService).getFollowers(testUser);
        verify(notificationService).notifyFollowersAboutNewPost(eq(testPost), anyList());
    }

    @Test
    void updatePost_ShouldUpdateExistingPost() {
        // Arrange
        Post updatedPost = Post.builder()
                .title("Updated Title")
                .content("Updated Content")
                .mediaType("image")
                .mediaUrl("/updated.jpg")
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        Post result = postService.updatePost(1L, updatedPost);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", testPost.getTitle());
        assertEquals("Updated Content", testPost.getContent());
        verify(postRepository).findById(1L);
        verify(postRepository).save(testPost);
    }

    @Test
    void updatePost_ShouldThrowException_WhenPostNotFound() {
        // Arrange
        Post updatedPost = Post.builder()
                .title("Updated Title")
                .content("Updated Content")
                .build();

        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            postService.updatePost(999L, updatedPost);
        });

        verify(postRepository).findById(999L);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void likePost_ShouldIncrementLikeCount_WhenNotAlreadyLiked() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postLikeRepository.existsByUserAndPost(testUser, testPost)).thenReturn(false);
        when(postLikeRepository.save(any(PostLike.class))).thenReturn(new PostLike());
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        Post result = postService.likePost(1L, testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, testPost.getLikeCount());
        verify(postLikeRepository).save(any(PostLike.class));
        verify(postRepository).save(testPost);
        verify(notificationService).notifyUserAboutPostLike(eq(testPost), eq(testUser));
    }

    @Test
    void likePost_ShouldNotIncrementLikeCount_WhenAlreadyLiked() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postLikeRepository.existsByUserAndPost(testUser, testPost)).thenReturn(true);

        // Act
        Post result = postService.likePost(1L, testUser);

        // Assert
        assertNotNull(result);
        assertEquals(0, testPost.getLikeCount()); // Should remain 0
        verify(postLikeRepository, never()).save(any(PostLike.class));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void unlikePost_ShouldDecrementLikeCount_WhenLiked() {
        // Arrange
        testPost.setLikeCount(1);
        PostLike postLike = new PostLike();

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postLikeRepository.findByUserAndPost(testUser, testPost))
                .thenReturn(Optional.of(postLike));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        Post result = postService.unlikePost(1L, testUser);

        // Assert
        assertNotNull(result);
        assertEquals(0, testPost.getLikeCount());
        verify(postLikeRepository).delete(postLike);
        verify(postRepository).save(testPost);
    }

    @Test
    void hidePost_ShouldSetIsHiddenToTrue() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        Post result = postService.hidePost(1L);

        // Assert
        assertNotNull(result);
        assertTrue(testPost.getIsHidden());
        verify(postRepository).save(testPost);
    }

    @Test
    void unhidePost_ShouldSetIsHiddenToFalse() {
        // Arrange
        testPost.setIsHidden(true);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        Post result = postService.unhidePost(1L);

        // Assert
        assertNotNull(result);
        assertFalse(testPost.getIsHidden());
        verify(postRepository).save(testPost);
    }

    @Test
    void hasUserLikedPost_ShouldReturnTrue_WhenUserLikedPost() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postLikeRepository.existsByUserAndPost(testUser, testPost)).thenReturn(true);

        // Act
        boolean result = postService.hasUserLikedPost(1L, testUser);

        // Assert
        assertTrue(result);
        verify(postLikeRepository).existsByUserAndPost(testUser, testPost);
    }

    @Test
    void hasUserLikedPost_ShouldReturnFalse_WhenUserHasNotLikedPost() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postLikeRepository.existsByUserAndPost(testUser, testPost)).thenReturn(false);

        // Act
        boolean result = postService.hasUserLikedPost(1L, testUser);

        // Assert
        assertFalse(result);
        verify(postLikeRepository).existsByUserAndPost(testUser, testPost);
    }

    @Test
    void deletePost_ShouldCallRepository() {
        // Arrange
        Long postId = 1L;

        // Act
        postService.deletePost(postId);

        // Assert
        verify(postRepository).deleteById(postId);
    }
}
