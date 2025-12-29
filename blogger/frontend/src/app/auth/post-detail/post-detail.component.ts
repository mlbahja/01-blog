import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { AuthService } from '../../core/services/auth.service';
import { PostService } from '../../core/services/post.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './post-detail.component.html',
  styleUrls: ['./post-detail.component.css'],
})
export class PostDetailComponent implements OnInit {
  postId: number = 0;
  post: any = null;
  loading: boolean = true;
  error: string = '';

  // For comments
  newComment: string = '';
  submittingComment: boolean = false;

  // For editing
  isEditing: boolean = false;
  editedTitle: string = '';
  editedContent: string = '';
  submittingEdit: boolean = false;

  // Related posts
  relatedPosts: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private postService: PostService,
    private authService: AuthService,
    private toastService: ToastService,
    private http: HttpClient,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.postId = +params['id'];
      this.loadPost();
    });
  }

  loadPost(): void {
    this.loading = true;
    this.error = '';

    this.postService.getPostById(this.postId).subscribe({
      next: (post) => {
        this.post = post;
        this.loading = false;
        this.loadRelatedPosts();
        console.log('Post loaded:', post);
      },
      error: (error) => {
        console.error('Error loading post:', error);
        this.error = 'Failed to load post. It might have been deleted.';
        this.loading = false;
        this.toastService.show(this.error, 'error');

        setTimeout(() => {
          this.router.navigate(['/home']);
        }, 3000);
      },
    });
  }

  loadRelatedPosts(): void {
    if (this.post && this.post.author) {
      this.postService.getPostsFromFollowedUsers(1, 5).subscribe({
        next: (posts) => {
          this.relatedPosts = posts.filter((p: any) => p.id !== this.postId).slice(0, 3);
        },
        error: (error) => {
          console.error('Error loading related posts:', error);
        },
      });
    }
  }
  calculateReadTime(content: string): number {
  if (!content) return 0;
  const words = content.trim().split(/\s+/).length;
  return Math.ceil(words / 200);
}

formatContent(content: string): SafeHtml {
  if (!content) {
    return '';
  }

  // Replace newlines with <br> tags
  const formattedContent = content.replace(/\n/g, '<br>');

  // Sanitize HTML to prevent XSS attacks
  return this.sanitizer.sanitize(1, formattedContent) || '';
}

sharePost(): void {
  this.copyPostLink();
}

shareOnTwitter(): void {
  const url = encodeURIComponent(window.location.href);
  window.open(`https://twitter.com/intent/tweet?url=${url}`, '_blank');
}

shareOnFacebook(): void {
  const url = encodeURIComponent(window.location.href);
  window.open(`https://www.facebook.com/sharer/sharer.php?u=${url}`, '_blank');
}

copyPostLink(): void {
  navigator.clipboard.writeText(window.location.href);
  this.toastService.show('Post link copied!', 'success');
}


  // Check if current user can delete this post
  canDeletePost(): boolean {
    if (!this.post || !this.post.author) return false;

    const currentUser = this.authService.getUserData();
    if (!currentUser) return false;

    const isOwner = this.post.author.id === currentUser.id;
    const isAdmin = currentUser.role === 'ADMIN';

    return isOwner || isAdmin;
  }

  // Check if current user can edit this post
  canEditPost(): boolean {
    return this.canDeletePost(); // Same permissions as delete
  }

  // Enable edit mode
  startEditing(): void {
    if (!this.canEditPost()) {
      this.toastService.show('You are not authorized to edit this post', 'error');
      return;
    }

    this.isEditing = true;
    this.editedTitle = this.post.title;
    this.editedContent = this.post.content;
  }

  // Cancel editing
  cancelEditing(): void {
    this.isEditing = false;
    this.editedTitle = '';
    this.editedContent = '';
  }

  // Save edited post
  saveEdit(): void {
    if (!this.editedTitle.trim() || !this.editedContent.trim()) {
      this.toastService.show('Title and content cannot be empty', 'error');
      return;
    }

    this.submittingEdit = true;

    const updatedPost = {
      title: this.editedTitle,
      content: this.editedContent,
      mediaType: this.post.mediaType,
      mediaUrl: this.post.mediaUrl
    };

    this.postService.updatePost(this.postId, updatedPost).subscribe({
      next: (response) => {
        this.toastService.show('Post updated successfully!', 'success');
        this.isEditing = false;
        this.submittingEdit = false;
        // Reload post to show updated content
        this.loadPost();
      },
      error: (error) => {
        console.error('Error updating post:', error);
        this.submittingEdit = false;
        this.toastService.show(error.error?.message || 'Failed to update post', 'error');
      }
    });
  }

  // Delete post from detail view
  confirmDelete(): void {
    if (!this.canDeletePost()) {
      this.toastService.show('You are not authorized to delete this post', 'error');
      return;
    }

    const confirmed = confirm(
      `Are you sure you want to delete "${this.post.title}"?\n\nThis action cannot be undone.`,
    );

    if (confirmed) {
      this.deletePost();
    }
  }

  deletePost(): void {
    const token = localStorage.getItem('jwt_token');

    if (!token) {
      this.toastService.show('Please login again', 'error');
      this.router.navigate(['/login']);
      return;
    }

    this.http
      .delete(`http://localhost:8080/auth/posts/${this.postId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      })
      .subscribe({
        next: (response: any) => {
          this.toastService.show('Post deleted successfully!', 'success');
          // Redirect to home
          setTimeout(() => {
            this.router.navigate(['/home']);
          }, 1500);
        },
        error: (error) => {
          console.error('Error deleting post:', error);
          this.toastService.show('Failed to delete post', 'error');
        },
      });
  }

  // Add comment
  addComment(): void {
    if (!this.newComment.trim()) {
      this.toastService.show('Please enter a comment', 'error');
      return;
    }

    this.submittingComment = true;

    this.postService
      .addComment(this.postId, {
        content: this.newComment,
      })
      .subscribe({
        next: () => {
          this.newComment = '';
          this.submittingComment = false;
          this.toastService.show('Comment added!', 'success');
          // Reload post to show new comment
          this.loadPost();
        },
        error: (error) => {
          console.error('Error adding comment:', error);
          this.submittingComment = false;
          this.toastService.show('Failed to add comment', 'error');
        },
      });
  }

  // Like post
  toggleLike(): void {
    if (this.post.isLiked) {
      this.postService.unlikePost(this.postId).subscribe({
        next: (updatedPost: any) => {
          this.post.likeCount = updatedPost.likeCount;
          this.post.isLiked = false;
        },
        error: (error) => {
          console.error('Error unliking post:', error);
        },
      });
    } else {
      this.postService.likePost(this.postId).subscribe({
        next: (updatedPost: any) => {
          this.post.likeCount = updatedPost.likeCount;
          this.post.isLiked = true;
          this.toastService.show('Post liked!', 'success');
        },
        error: (error) => {
          console.error('Error liking post:', error);
        },
      });
    }
  }

  // Share functionality
  /*
  sharePost(): void {
    const url = window.location.href;
    const title = this.post.title;

    if (navigator.share) {
      // Use Web Share API if available
      navigator.share({
        title: title,
        text: `Check out this post: ${title}`,
        url: url,
      });
    } else {
      // Fallback: copy to clipboard
      navigator.clipboard.writeText(`${title}\n${url}`).then(() => {
        this.toastService.show('Post link copied to clipboard!', 'success');
      });
    }
  }*/
  viewPostDetails(postId: number) {
    this.router.navigate(['/post', postId]);
  }

  // Navigation
  goBack(): void {
    this.router.navigate(['/home']);
  }

  // Open in new tab
  openInNewTab(): void {
    window.open(window.location.href, '_blank');
  }

  // Get author's other posts
  getAuthorPosts(): void {
    if (this.post && this.post.author) {
      this.router.navigate(['/home'], {
        queryParams: { author: this.post.author.id },
      });
    }
  }
}
