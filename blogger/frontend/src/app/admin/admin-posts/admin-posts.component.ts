import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../core/services/admin.service';
import { ToastService } from '../../core/services/toast.service';
import { Post } from '../../core/models/post.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';



@Component({
    selector: 'app-admin-posts',
    templateUrl: './admin-posts.component.html',
    styleUrls: ['./admin-posts.component.css'],
    standalone: true, // <-- hna
    imports: [CommonModule, FormsModule, RouterModule] 
})

export class AdminPostsComponent implements OnInit {
    posts: any[] = [];
    loading = false;

    constructor(private adminService: AdminService, private toastService: ToastService) { }

    ngOnInit(): void {
        this.loadPosts();
    }

    //   loadPosts(): void {
    //     this.loading = true;
    //     this.adminService.getAllPosts().subscribe({
    //       next: (res: any) => {
    //         this.posts = res.posts || res.content || res;
    //         this.loading = false;
    //       },
    //       error: () => {
    //         this.toastService.show('Failed to load posts', 'error');
    //         this.loading = false;
    //       },
    //     });
    //   }
    loadPosts(): void {
        this.loading = true;
        this.adminService.getAllPosts().subscribe({
            next: (posts: Post[]) => {   // Posts array directly
                this.posts = posts;
                this.loading = false;
                console.log(posts)
            },
            error: () => {
                this.toastService.show('Failed to load posts', 'error');
                this.loading = false;
            },
        });
    }
    // hidePost(postId: number): void {
    //     this.adminService.hidePost(postId).subscribe(() => {
    //         this.toastService.show('Post hidden', 'success');
    //         this.loadPosts();
    //     });
    // }

    // unhidePost(postId: number): void {
    //     this.adminService.unhidePost(postId).subscribe(() => {
    //         this.toastService.show('Post visible', 'success');
    //         this.loadPosts();
    //     });
    // }
    hidePost(postId: number): void {
    this.adminService.hidePost(postId).subscribe({
        next: () => {
            this.toastService.show('Post hidden', 'success');
            this.posts = this.posts.map(post =>
                post.id === postId ? { ...post, isHidden: true } : post
            );
        },
        error: () => this.toastService.show('Failed to hide post', 'error')
    });
}

unhidePost(postId: number): void {
    this.adminService.unhidePost(postId).subscribe({
        next: () => {
            this.toastService.show('Post visible', 'success');
            this.posts = this.posts.map(post =>
                post.id === postId ? { ...post, isHidden: false } : post
            );
        },
        error: () => this.toastService.show('Failed to unhide post', 'error')
    });
}

    deletePost(postId: number): void {
        if (!confirm('Are you sure you want to delete this post?')) return;
        this.adminService.deletePost(postId).subscribe(() => {
            this.toastService.show('Post deleted', 'success');
            this.loadPosts();
        });
    }
}
