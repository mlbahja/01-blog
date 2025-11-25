import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { PostService } from '../../core/services/post.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
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

  constructor(
    private authService: AuthService,
    private postService: PostService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    // Get the logged in user's information
    this.username = localStorage.getItem('username') || '';
    this.loadPosts();
  }

  loadPosts(): void {
    this.postService.getAllPosts().subscribe({
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

  createPost(): void {
    if (this.newPost.title && this.newPost.content) {
      this.postService
        .createPost(this.newPost)
        .subscribe({
          next: () => {
            this.loadPosts();
            // Reset form
            this.newPost = { title: '', content: '' };
            this.showCreateForm = false;
            this.toastService.show('Post published successfully!', 'success');
          },
          error: (error: any) => {
            console.error('Error creating post:', error);
            this.toastService.show('Failed to create post', 'error');
          },
        });
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
}
