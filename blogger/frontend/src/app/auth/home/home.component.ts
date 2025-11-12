import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { PostService } from '../../core/services/post.service';

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

  constructor(
    private authService: AuthService,
    private postService: PostService,
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
      },
      error: (error: any) => {
        console.error('Error loading posts:', error);
      },
    });
  }

  createPost(): void {
    if (this.newPost.title && this.newPost.content) {
      this.postService
        .createPost({
          ...this.newPost,
          author: this.username,
        })
        .subscribe({
          next: () => {
            this.loadPosts();
            // Reset form
            this.newPost = { title: '', content: '' };
          },
          error: (error: any) => {
            console.error('Error creating post:', error);
          },
        });
    }
  }

  addComment(postId: number, commentText: string): void {
    if (commentText) {
      this.postService
        .addComment(postId, {
          content: commentText,
          author: this.username,
        })
        .subscribe({
          next: () => {
            this.loadPosts(); // Reload to show new comment
          },
          error: (error: any) => {
            console.error('Error adding comment:', error);
          },
        });
    }
  }

  logout(): void {
    this.authService.logout();
  }
}
