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
    mediaType: '',
    mediaUrl: '',
  };
  selectedFile: File | null = null;
  uploading = false;
  showCreateForm = false;
  expandedPosts = new Set<number>();
  showFollowedOnly = false;
  currentPage: number = 1;
  pageSize: number = 10;
  totalPosts: number = 0;

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
/*
  loadPosts(): void {
    const postsObservable = this.showFollowedOnly
      ? this.postService.getPostsFromFollowedUsers(1,10)
      : this.postService.getAllPosts(1,10);

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
  }*/
loadPosts(): void {
  const postsObservable = this.showFollowedOnly
    ? this.postService.getPostsFromFollowedUsers(this.currentPage, this.pageSize)
    : this.postService.getAllPosts(this.currentPage, this.pageSize);

  postsObservable.subscribe({
    next: (response: any) => {
      console.log("Response from backend:", response);

      // Support different backend formats
      this.posts = response.posts || response.content || response;

      // Very important!!
      this.totalPosts = response.total || response.totalElements || 0;

      // Load liked status
      this.posts.forEach(post => {
        this.postService.hasLikedPost(post.id).subscribe({
          next: liked => post.isLiked = liked,
          error: () => post.isLiked = false
        });
      });
    },
    error: () => {
      this.toastService.show("Failed to load posts", "error");
    }
  });
}

nextPage() {
  if (this.currentPage * this.pageSize < this.totalPosts) {
    this.currentPage++;
    this.loadPosts();
  }
}

prevPage() {
  if (this.currentPage > 1) {
    this.currentPage--;
    this.loadPosts();
  }
}

  toggleFeedFilter(): void {
    this.showFollowedOnly = !this.showFollowedOnly;
    this.loadPosts();
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  removeFile(): void {
    this.selectedFile = null;
    this.newPost.mediaType = '';
    this.newPost.mediaUrl = '';
  }

  createPost(): void {
    if (this.newPost.title && this.newPost.content) {
      console.log('[HomeComponent] Creating post with data:', this.newPost);
      console.log('[HomeComponent] Selected file:', this.selectedFile);

      // If file is selected, upload it first
      if (this.selectedFile) {
        console.log('[HomeComponent] Starting file upload...');
        console.log('[HomeComponent] File size:', this.selectedFile.size, 'bytes');
        console.log('[HomeComponent] File type:', this.selectedFile.type);

        this.uploading = true;
        this.postService.uploadMedia(this.selectedFile).subscribe({
          next: (uploadResponse) => {
            console.log('[HomeComponent] File uploaded successfully:', uploadResponse);
            this.newPost.mediaUrl = uploadResponse.url;
            this.newPost.mediaType = uploadResponse.mediaType;
            this.submitPost();
          },
          error: (error: any) => {
            console.error('[HomeComponent] Error uploading file:', error);
            console.error('[HomeComponent] Error status:', error.status);
            console.error('[HomeComponent] Error message:', error.message);
            console.error('[HomeComponent] Error details:', error.error);
            this.uploading = false;

            let errorMsg = 'Failed to upload media file';
            if (error.status === 413) {
              errorMsg = 'File is too large. Maximum size is 10MB';
            } else if (error.error?.message) {
              errorMsg = error.error.message;
            }

            this.toastService.show(errorMsg, 'error');
          }
        });
      } else {
        // No file, just create the post
        this.submitPost();
      }
    } else {
      console.warn('[HomeComponent] Title or content is empty');
      this.toastService.show('Please fill in both title and content', 'error');
    }
  }

  private submitPost(): void {
    this.postService.createPost(this.newPost).subscribe({
      next: (response) => {
        console.log('[HomeComponent] Post created successfully:', response);
        this.loadPosts();
        // Reset form
        this.newPost = { title: '', content: '', mediaType: '', mediaUrl: '' };
        this.selectedFile = null;
        this.uploading = false;
        this.showCreateForm = false;
        this.toastService.show('Post published successfully!', 'success');
      },
      error: (error: any) => {
        console.error('[HomeComponent] Error creating post:', error);
        this.uploading = false;

        if (error.status === 403) {
          this.toastService.show('Not authorized. Please login again.', 'error');
        } else if (error.status === 401) {
          this.toastService.show('Session expired. Please login again.', 'error');
        } else {
          this.toastService.show('Failed to create post: ' + (error.error?.message || error.message), 'error');
        }
      }
    });
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
