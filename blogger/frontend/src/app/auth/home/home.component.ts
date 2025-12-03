import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { PostService } from '../../core/services/post.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  username: string = '';
  posts: any[] = [];
  newPost = {
    title: '',
    content: '',
  };
  showCreateForm = false;
  expandedPosts = new Set<number>();
  showFollowedOnly = false;

  constructor(
    private authService: AuthService,
    private postService: PostService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    // Get the logged in user's information
    const userData = this.authService.getUserData();
    this.username = userData?.username || 'Guest';
    this.loadPosts();
  }

  loadPosts(): void {
    const postsObservable = this.showFollowedOnly
      ? this.postService.getPostsFromFollowedUsers()
      : this.postService.getAllPosts();

    postsObservable.subscribe({
      next: (posts: any) => {
        this.posts = posts;
        // Load liked status for each post
        this.posts.forEach(post => {
          this.postService.hasLikedPost(post.id).subscribe({
            next: (liked: boolean) => {
              post.isLiked = liked;
            },
            error: () => {
              post.isLiked = false;
            }
          });
        });
      },
      error: (error: any) => {
        console.error('Error loading posts:', error);
        this.toastService.show('Failed to load posts', 'error');
      },
    });
  }

  toggleFeedFilter(): void {
    this.showFollowedOnly = !this.showFollowedOnly;
    this.loadPosts();
  }

  createPost(): void {
    if (this.newPost.title && this.newPost.content) {
      console.log('[HomeComponent] Creating post with data:', this.newPost);
      console.log('[HomeComponent] Token from localStorage:', localStorage.getItem('jwt_token'));

      this.postService
        .createPost(this.newPost)
        .subscribe({
          next: (response) => {
            console.log('[HomeComponent] Post created successfully:', response);
            this.loadPosts();
            // Reset form
            this.newPost = { title: '', content: '' };
            this.showCreateForm = false;
            this.toastService.show('Post published successfully!', 'success');
          },
          error: (error: any) => {
            console.error('[HomeComponent] Error creating post:', error);
            console.error('[HomeComponent] Error status:', error.status);
            console.error('[HomeComponent] Error message:', error.message);
            console.error('[HomeComponent] Error body:', error.error);

            if (error.status === 403) {
              this.toastService.show('Not authorized. Please login again.', 'error');
            } else if (error.status === 401) {
              this.toastService.show('Session expired. Please login again.', 'error');
            } else {
              this.toastService.show('Failed to create post: ' + (error.error?.message || error.message), 'error');
            }
          },
        });
    } else {
      console.warn('[HomeComponent] Title or content is empty');
      this.toastService.show('Please fill in both title and content', 'error');
    }
  }

  addComment(postId: number, commentText: string): void {
    if (commentText) {
      this.postService
        .addComment(postId, {
          content: commentText,
        })
        .subscribe({
          next: () => {
            this.loadPosts(); // Reload to show new comment
            this.toastService.show('Comment added!', 'success');
          },
          error: (error: any) => {
            console.error('Error adding comment:', error);
            this.toastService.show('Failed to add comment', 'error');
          },
        });
    }
  }

  toggleLike(post: any): void {
    if (post.isLiked) {
      // Unlike the post
      this.postService.unlikePost(post.id).subscribe({
        next: (updatedPost: any) => {
          post.likeCount = updatedPost.likeCount;
          post.isLiked = false;
        },
        error: (error: any) => {
          console.error('Error unliking post:', error);
          this.toastService.show('Failed to unlike post', 'error');
        },
      });
    } else {
      // Like the post
      this.postService.likePost(post.id).subscribe({
        next: (updatedPost: any) => {
          post.likeCount = updatedPost.likeCount;
          post.isLiked = true;
          this.toastService.show('Post liked!', 'success');
        },
        error: (error: any) => {
          console.error('Error liking post:', error);
          this.toastService.show('Failed to like post', 'error');
        },
      });
    }
  }

  expandPost(postIndex: number): void {
    if (this.expandedPosts.has(postIndex)) {
      this.expandedPosts.delete(postIndex);
    } else {
      this.expandedPosts.add(postIndex);
    }
  }

  logout(): void {
    this.authService.logout();
  }

  isAdmin(): boolean {
    const userData = this.authService.getUserData();
    return userData && userData.role === 'ADMIN';
  }
}
