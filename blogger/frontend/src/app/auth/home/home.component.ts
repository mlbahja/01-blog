import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { PostService } from '../../core/services/post.service';
import { ToastService } from '../../core/services/toast.service';
import { NotificationService } from '../../core/services/notification.service';
import { ReportService } from '../../core/services/report.service';
import { HttpClient } from '@angular/common/http';
import { Route } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit, OnDestroy {
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

  // Notification properties
  unreadNotificationCount: number = 0;
  private notificationSubscription?: Subscription;

  // Report properties
  showReportDialog = false;
  reportingPost: any = null;
  reportReason: string = '';

  constructor(
    private authService: AuthService,
    private postService: PostService,
    private toastService: ToastService,
    private notificationService: NotificationService,
    private reportService: ReportService,
    private http: HttpClient,
    private router: Router,
  ) {}

  ngOnInit(): void {
    // Get the logged in user's information
    const userData = this.authService.getUserData();
    this.username = userData?.username || 'Guest';
    this.loadPosts();

    // Subscribe to notification count updates
    this.notificationSubscription = this.notificationService.unreadCount$.subscribe(
      count => {
        this.unreadNotificationCount = count;
      }
    );
  }

  ngOnDestroy(): void {
    // Clean up subscription
    if (this.notificationSubscription) {
      this.notificationSubscription.unsubscribe();
    }
  }



  
  loadPosts(): void {
    const postsObservable = this.showFollowedOnly
      ? this.postService.getPostsFromFollowedUsers(this.currentPage, this.pageSize)
      : this.postService.getAllPosts(this.currentPage, this.pageSize);

    postsObservable.subscribe({
      next: (response: any) => {
        console.log('Response from backend:', response);

        this.posts = response.posts || response.content || response;
        this.totalPosts = response.total || response.totalElements || 0;

        this.posts.forEach((post) => {
          this.postService.hasLikedPost(post.id).subscribe({
            next: (liked) => (post.isLiked = liked),
            error: () => (post.isLiked = false),
          });
        });
      },
      error: () => {
        this.toastService.show('Failed to load posts', 'error');
      },
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
      if (this.selectedFile) {
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
            this.uploading = false;

            let errorMsg = 'Failed to upload media file';
            if (error.status === 413) {
              errorMsg = 'File is too large. Maximum size is 10MB';
            } else if (error.error?.message) {
              errorMsg = error.error.message;
            }

            this.toastService.show(errorMsg, 'error');
          },
        });
      } else {
        this.submitPost();
      }
    } else {
      this.toastService.show('Please fill in both title and content', 'error');
    }
  }

  private submitPost(): void {
    this.postService.createPost(this.newPost).subscribe({
      next: (response) => {
        console.log('[HomeComponent] Post created successfully:', response);
        this.loadPosts();
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
          this.toastService.show(
            'Failed to create post: ' + (error.error?.message || error.message),
            'error',
          );
        }
      },
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
  viewPostDetails(postId: number): void {
    this.router.navigate(['/post', postId]);
  }

  logout(): void {
    this.authService.logout();
  }

  isAdmin(): boolean {
    const userData = this.authService.getUserData();
    return userData && userData.role === 'ADMIN';
  }

  canDeletePost(post: any): boolean {
    const userData = this.authService.getUserData();

    if (!userData) {
      return false;
    }

    const isAdmin = userData.role === 'ADMIN';
    if (isAdmin) {
      return true;
    }

    if (!post.author) {
      return false;
    }

    const isOwner = post.author.id === userData.id;
    return isOwner;
  }

  confirmDelete(postId: number, postTitle: string) {
    const confirmDelete = confirm(
      `Are you sure you want to delete "${postTitle}"?\n\nThis action cannot be undone.`,
    );

    if (confirmDelete) {
      this.deletePost(postId);
    }
  }

  deletePost(postId: number) {
    const token = localStorage.getItem('jwt_token');

    if (!token) {
      this.toastService.show('Please login again', 'error');
      this.router.navigate(['/login']);
      return;
    }

    const headers = {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    };

    this.http.delete(`http://localhost:8080/auth/posts/${postId}`, { headers }).subscribe({
      next: (response: any) => {
        console.log('Post deleted:', response);

        this.posts = this.posts.filter((p) => p.id !== postId);

        if (this.posts.length === 0) {
          this.loadPosts();
        }

        this.toastService.show('Post deleted successfully!', 'success');
      },
      error: (error: any) => {
        console.error('Error deleting post:', error);

        let errorMessage = 'Failed to delete post';
        if (error.status === 403) {
          errorMessage = 'You are not authorized to delete this post';
        } else if (error.status === 404) {
          errorMessage = 'Post not found';
        } else if (error.status === 401) {
          errorMessage = 'Please login again';
          this.router.navigate(['/login']);
        }

        this.toastService.show(errorMessage, 'error');
      },
    });
  }

  testButtonClick(postId: number, postTitle: string) {
    const post = this.posts.find((p) => p.id === postId);
    if (post) {
      const canDelete = this.canDeletePost(post);
      if (canDelete) {
        this.confirmDelete(postId, postTitle);
      }
    }
  }

  isMyPost(post: any): boolean {
    const userData = this.authService.getUserData();
    return post.author?.id === userData?.id;
  }

  openReportDialog(post: any): void {
    this.reportingPost = post;
    this.reportReason = '';
    this.showReportDialog = true;
  }

  closeReportDialog(): void {
    this.showReportDialog = false;
    this.reportingPost = null;
    this.reportReason = '';
  }

  submitReport(): void {
    if (!this.reportReason.trim() || !this.reportingPost) {
      return;
    }

    const reportData = {
      postId: this.reportingPost.id,
      message: this.reportReason.trim()
    };

    this.reportService.createReport(reportData).subscribe({
      next: () => {
        this.toastService.show('Report submitted successfully', 'success');
        this.closeReportDialog();
      },
      error: (err) => {
        console.error('Error submitting report:', err);
        this.toastService.show(err.error?.error || 'Failed to submit report', 'error');
      }
    });
  }
}
