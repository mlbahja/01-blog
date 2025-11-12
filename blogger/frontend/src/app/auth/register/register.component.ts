import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
 
 
    username = '';
    email = '';
    password = '';
    confirmPassword = '';

  constructor(private authService: AuthService) {}

  onSubmit() {
    if (this.password !== this.confirmPassword) {
      console.log('Passwords do not match!');
      return;
    }
    const user = {
      username: this.username,
      email: this.email,
      password: this.password,
    };
    this.authService.register(user).subscribe({
      next: (res: any) => {
        console.log('user are registered', res);
        // Navigate to login after successful registration
        this.authService.navigateToLogin();
      },
      error: (err: any) => console.log('Registration error', err),
    });
  }
}
