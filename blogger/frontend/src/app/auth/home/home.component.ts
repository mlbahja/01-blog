/////////////////////////////////////////////////////////////
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { PostService } from '../../core/services/post.service';
import { ToastService } from '../../core/services/toast.service';
import { HttpClient } from '@angular/common/http'; // Add this import
import { Route } from '@angular/router';

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
    private toastService: ToastService,
    private http: HttpClient, // Add HttpClient to constructor
    private router: Router, // Add Router if not already there
  ) {}

  ngOnInit(): void {
    // Get the logged in user's information
    const userData = this.authService.getUserData();
    this.username = userData?.username || 'Guest';
    this.loadPosts();
  }

  loadPosts(): void {
    const postsObservable = this.showFollowedOnly
      ? this.postService.getPostsFromFollowedUsers(this.currentPage, this.pageSize)
      : this.postService.getAllPosts(this.currentPage, this.pageSize);

    postsObservable.subscribe({
      next: (response: any) => {
        console.log('Response from backend:', response);

        // Support different backend formats
        this.posts = response.posts || response.content || response;

        // Very important!!
        this.totalPosts = response.total || response.totalElements || 0;

        // Load liked status
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
          },
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

  // ========== DELETE POST METHODS ==========

  canDeletePost(post: any): boolean {
    // Quick debug
    console.log('🔍 Checking delete permission for post:', post.id);

    // Get current user
    const userData = this.authService.getUserData();

    if (!userData) {
      console.log('❌ No user data');
      return false;
    }

    // Check if user is ADMIN
    const isAdmin = userData.role === 'ADMIN';
    if (isAdmin) {
      console.log('✅ User is ADMIN - can delete any post');
      return true;
    }

    // Check if post has author info
    if (!post.author) {
      console.log('❌ Post has no author info');
      return false;
    }

    // Check if current user is the author
    const isOwner = post.author.id === userData.id;

    console.log(
      `📊 Check: Post author ID (${post.author.id}) === User ID (${userData.id}) = ${isOwner}`,
    );
    console.log(`📊 Result: ${isOwner ? '✅ Can delete (owner)' : '❌ Cannot delete (not owner)'}`);

    return isOwner;
  }

  // ========== DELETE POST METHODS ==========
  // ========== broutforce delet Posts
  /*
  testDeleteAnyPost() {
    if (this.posts.length > 1) {
      const testPost = this.posts[1];
      console.log('Testing delete on post:', testPost);

      // First check permission
      const canDelete = this.canDeletePost(testPost);
      console.log('Can delete according to check:', canDelete);

      if (canDelete) {
        this.confirmDelete(testPost.id, testPost.title);
      } else {
        console.log('Cannot delete - showing why...');

        // Check what's in the post
        console.log('Post author structure:');
        console.log('- Type:', typeof testPost.author);
        console.log('- Value:', testPost.author);
        console.log('- Keys:', testPost.author ? Object.keys(testPost.author) : 'No author');

        // Check user data
        const userData = this.authService.getUserData();
        console.log('User data:', userData);

        // Try force delete anyway for testing
        const forceDelete = confirm(
          'Permission check failed, but try to delete anyway? (FOR TESTING)',
        );
        if (forceDelete) {
          this.deletePost(testPost.id);
        }
      }
    }
  }*/

  confirmDelete(postId: number, postTitle: string) {
    const confirmDelete = confirm(
      `Are you sure you want to delete "${postTitle}"?\n\nThis action cannot be undone.`,
    );

    if (confirmDelete) {
      this.deletePost(postId);
    }
  }

  deletePost(postId: number) {
    // Get auth token from localStorage or auth service
    const token = localStorage.getItem('jwt_token'); // or 'auth_token' based on your storage

    // Check if token exists
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

        // Remove the deleted post from the posts array
        this.posts = this.posts.filter((p) => p.id !== postId);

        // If posts array becomes empty, handle appropriately
        if (this.posts.length === 0) {
          this.loadPosts(); // Reload posts or show empty state
        }

        // Show success message
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
          // Redirect to login
          this.router.navigate(['/login']);
        }

        this.toastService.show(errorMessage, 'error');
      },
    });
  }

  // Optional: You can use these if you don't want to use ToastService
  private showSuccessMessage(message: string) {
    alert('✅ ' + message);
  }

  private showErrorMessage(message: string) {
    alert('❌ ' + message);
  }
  ////////////////////////////////////////////////////////////debug test
  // Add this method to test button clicks
  testButtonClick(postId: number, postTitle: string) {
    // console.log('🔥 BUTTON CLICKED!', postId, postTitle);
    // alert(`Button clicked! Post: ${postTitle} (ID: ${postId})`);

    // Test if canDeletePost returns true
    const post = this.posts.find((p) => p.id === postId);
    if (post) {
      //should remove this alert its not good practice to add alert for every transaction

      const canDelete = this.canDeletePost(post);
      // alert(`canDeletePost returns: ${canDelete}`);

      if (canDelete) {
        this.confirmDelete(postId, postTitle);
      }
    }
  }
  /*
  debugLoginState() {
    console.log('=== DEBUG LOGIN STATE ===');

    // Check localStorage directly
    console.log('localStorage contents:');
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);
      const value = localStorage.getItem(key!);
      console.log(`${key}: ${value?.substring(0, 50)}...`);
    }

    // Check AuthService
    console.log('\nAuthService:');
    console.log('- getToken():', this.authService.getToken() ? 'Present' : 'Missing');
    console.log('- getUserData():', this.authService.getUserData());
    console.log('- isLoggedIn():', this.authService.isLoggedIn());

    // Check if token is valid
    const token = this.authService.getToken();
    if (token) {
      console.log('\nToken details:');
      console.log('- Length:', token.length);
      console.log('- First 50 chars:', token.substring(0, 50) + '...');

      // Try to decode JWT token (if it's a JWT)
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        console.log('- Token payload:', payload);
        console.log('- Token expiry:', new Date(payload.exp * 1000));
        console.log('- Token username:', payload.sub);
      } catch (e) {
        console.log('- Not a standard JWT token');
      }
    }
  }*/
  ///debug post
  /*
  debugPosts() {
    console.log('=== DEBUG POSTS ===');
    console.log('Total posts:', this.posts.length);

    if (this.posts.length > 0) {
      console.log('First post (index 0 - featured):', this.posts[0]);
      console.log('Post author details:', this.posts[0].author);

      if (this.posts.length > 1) {
        console.log('Second post (index 1 - regular):', this.posts[1]);
        console.log('Post author details:', this.posts[1].author);

        // Check if can delete
        console.log('Can delete second post?', this.canDeletePost(this.posts[1]));
      }
    }

    //forceing delet post using brodforce
    // Add this method for testing
  }
    */
}
