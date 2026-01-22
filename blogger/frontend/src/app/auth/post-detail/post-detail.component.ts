import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { AuthService } from '../../core/services/auth.service';
import { PostService } from '../../core/services/post.service';
import { ToastService } from '../../core/services/toast.service';
import { UserProfile } from '../../core/models/user.model';



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
  comments: any[] = [];
  commentsTotal: number = 0;
  commentsTotalPages: number = 0;
  commentsCurrentPage: number = 0;
  commentPageSize: number = 5;

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
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.postId = + params['id'];
      this.loadPost();
    });
  }

  loadPost(): void {
    this.loading = true;
    this.error = '';
    console.log("====>  ", this.post);
    this.postService.getPostById(this.postId).subscribe({
      next: (post) => {
        this.post = post;
        this.comments = post.comments || [];
        this.commentsTotal = post.comments?.length || 0;
        this.loading = false;
        this.loadRelatedPosts();
        this.loadComments(0, false);
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

  //  canDeletComment(post:any): boolean {
  //     const userData = this.authService.getUserData();

  //     if (!userData) {
  //       return false;
  //     }

  //     const isAdmin = userData.role === 'ADMIN';
  //     if (isAdmin) {
  //       return true;
  //     }

  //     if (!post.author) {
  //       return false;
  //     }

  //     const isOwner = post.author.id === userData.id;
  //     return isOwner;
  //   }
  //   confirmDeleteComment(postId: number, postTitle: string) {
  //     const confirmDelete = confirm(
  //       `Are you sure you want to delete "${postTitle}"?\n\nThis action cannot be undone.`,
  //     );

  //     if (confirmDelete) {
  //       this.deletePost(postId);
  //     }
  //   }

  // Add this method to check if user can delete a comment
  canDeleteComment(comment: any): boolean {
    const userData = this.authService.getUserData();

    if (!userData) {
      return false;
    }

    const isAdmin = userData.role === 'ADMIN';
    if (isAdmin) {
      return true;
    }

    if (!comment.author) {
      return false;
    }

    const isOwner = comment.author.id === userData.id;
    return isOwner;
  }

  /////////////////////////////////////

  // Add method to delete a comment
  // In PostDetailComponent
  deleteComment(commentId: number): void {
    const confirmDelete = confirm('Are you sure you want to delete this comment?');

    if (!confirmDelete) {
      return;
    }

    this.postService.deleteComment(this.postId, commentId).subscribe({
      next: () => {
        this.toastService.show('Comment deleted successfully!', 'success');
        this.loadComments(0, false);
      },
      error: (error: any) => {
        console.error('Error deleting comment:', error);
        this.toastService.show('Failed to delete comment', 'error');
      },
    });
  }

  loadComments(page: number = 0, append: boolean = false): void {
    this.postService.getComments(this.postId, page, this.commentPageSize).subscribe({
      next: (response: any) => {
        const comments = response.comments || [];
        if (!append) {
          this.comments = comments;
        } else {
          this.comments = this.comments.concat(comments);
        }
        this.commentsTotal = response.total || 0;
        this.commentsTotalPages = response.totalPages || 0;
        this.commentsCurrentPage = response.currentPage || page;
      },
      error: () => {
        this.toastService.show('Failed to load comments', 'error');
      },
    });
  }

  loadMoreComments(): void {
    const nextPage = this.commentsCurrentPage + 1;
    if (nextPage >= this.commentsTotalPages) {
      return;
    }
    this.loadComments(nextPage, true);
  }

  // Also add this method to check if it's your own comment
  isMyComment(comment: any): boolean {
    const userData = this.authService.getUserData();
    return comment.author?.id === userData?.id;
  }

  loadRelatedPosts(): void {
    if (this.post && this.post.author) {
      console.log(" dkhola lhna=============+> " + this.post);


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

  getProfilePictureUrl(): string {
    const author = this.post?.author;

    if (!author) {
      return this.getDefaultAvatar();
    }

    const imageUrl = author.profilePictureUrl || author.avatar;

    if (imageUrl && imageUrl.startsWith('/uploads/')) {
      return 'http://localhost:8080' + imageUrl;
    }

    return imageUrl || this.getDefaultAvatar();
  }




  getDefaultAvatar(): string {
    // Return a data URI for a simple gray circle with a user icon
    return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTUwIiBoZWlnaHQ9IjE1MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTUwIiBoZWlnaHQ9IjE1MCIgZmlsbD0iI2UwZTBlMCIvPjxjaXJjbGUgY3g9Ijc1IiBjeT0iNTUiIHI9IjI1IiBmaWxsPSIjOTk5Ii8+PHBhdGggZD0iTTMwIDEyMGMwLTI1IDIwLTQ1IDQ1LTQ1czQ1IDIwIDQ1IDQ1IiBmaWxsPSIjOTk5Ii8+PC9zdmc+';
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
  getUserProfilePicture(user: any): string {
    if (!user) {
      return this.getDefaultAvatar();
    }

    const imageUrl = user.profilePictureUrl || user.avatar;

    if (imageUrl && imageUrl.startsWith('/uploads/')) {
      return 'http://localhost:8080' + imageUrl;
    }

    return imageUrl || this.getDefaultAvatar();
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
          this.loadComments(0, false);
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
