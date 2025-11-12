import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { RouterModule } from '@angular/router';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  username = '';
  password = '';

  constructor(private authService: AuthService,private toastService: ToastService) {}

  onSubmit() {
    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: (res: any) => {
        console.log('Login successful!');
         this.toastService.show('✅ Login successful!', 'success');
      },
      error: (err: any) => {
        this.toastService.show('❌ Invalid username or password!', 'error');
      },
    });
  }
}
