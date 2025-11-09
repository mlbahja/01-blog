import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  username: string = '';

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    // Get the logged in user's information
    this.username = localStorage.getItem('username') || '';
  }

  logout(): void {
    this.authService.logout();
  }
}
