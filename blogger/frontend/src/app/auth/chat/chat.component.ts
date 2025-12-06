import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { MessageService } from '../../core/services/message.service';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css'],
})
export class ChatComponent implements OnInit {
  conversations: any[] = [];
  selectedConversation: any = null;
  messages: any[] = [];
  newMessage = '';
  currentUserId: number = 0;
  loading = true;
  sendingMessage = false;

  constructor(
    private messageService: MessageService,
    private userService: UserService,
    private authService: AuthService,
    private toastService: ToastService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const userData = this.authService.getUserData();
    this.currentUserId = userData?.id || 0;
    this.loadConversations();

    // Check if userId query parameter is provided
    this.route.queryParams.subscribe(params => {
      const userId = params['userId'];
      if (userId) {
        // Find or create conversation with this user
        this.startConversationWithUser(Number(userId));
      }
    });
  }

  startConversationWithUser(userId: number): void {
    // Get user info first
    this.userService.getUserProfile(userId).subscribe({
      next: (user: any) => {
        // Check if conversation already exists
        const existingConv = this.conversations.find(c => c.userId === userId);
        if (existingConv) {
          this.selectConversation(existingConv);
        } else {
          // Create a new conversation object
          const newConv = {
            userId: user.id,
            username: user.username,
            email: user.email,
            unreadCount: 0
          };
          this.selectConversation(newConv);
        }
      },
      error: (error: any) => {
        console.error('Error loading user:', error);
        this.toastService.show('Failed to start conversation', 'error');
      }
    });
  }

  loadConversations(): void {
    this.loading = true;
    this.messageService.getConversations().subscribe({
      next: (conversations: any) => {
        this.conversations = conversations;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading conversations:', error);
        this.toastService.show('Failed to load conversations', 'error');
        this.loading = false;
      },
    });
  }

  selectConversation(conversation: any): void {
    this.selectedConversation = conversation;
    this.loadMessages(conversation.userId);
    this.markAsRead(conversation.userId);
  }

  loadMessages(userId: number): void {
    this.messageService.getConversation(userId).subscribe({
      next: (messages: any) => {
        this.messages = messages;
        // Scroll to bottom after loading messages
        setTimeout(() => this.scrollToBottom(), 100);
      },
      error: (error: any) => {
        console.error('Error loading messages:', error);
        this.toastService.show('Failed to load messages', 'error');
      },
    });
  }

  sendMessage(): void {
    if (!this.newMessage.trim() || !this.selectedConversation) {
      return;
    }

    this.sendingMessage = true;
    this.messageService
      .sendMessage(this.selectedConversation.userId, this.newMessage)
      .subscribe({
        next: (response: any) => {
          this.newMessage = '';
          this.loadMessages(this.selectedConversation.userId);
          this.loadConversations(); // Refresh conversation list
          this.sendingMessage = false;
        },
        error: (error: any) => {
          console.error('Error sending message:', error);
          this.toastService.show('Failed to send message', 'error');
          this.sendingMessage = false;
        },
      });
  }

  markAsRead(userId: number): void {
    this.messageService.markAsRead(userId).subscribe({
      next: () => {
        // Update unread count in conversation list
        const conv = this.conversations.find((c) => c.userId === userId);
        if (conv) {
          conv.unreadCount = 0;
        }
      },
      error: (error: any) => {
        console.error('Error marking messages as read:', error);
      },
    });
  }

  scrollToBottom(): void {
    const messageContainer = document.querySelector('.messages-container');
    if (messageContainer) {
      messageContainer.scrollTop = messageContainer.scrollHeight;
    }
  }

  isAdmin(): boolean {
    const userData = this.authService.getUserData();
    return userData && userData.role === 'ADMIN';
  }

  logout(): void {
    this.authService.logout();
  }
}
