import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-welcome',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mt-4">
      <h2>Zdravo, {{ userName }}!</h2>
    </div>
  `,
  styles: []
})
export class WelcomeComponent implements OnInit {
  userName: string | null = '';

  constructor(private authService: AuthService) {}

  async ngOnInit() {
    try {
      if (this.authService.tokenExists()) {  // Prvo proveravamo da li token postoji
        const userInfo = await this.authService.getCurrentUser();
        this.userName = userInfo.firstName;
      } else {
        this.userName = 'Guest';
      }
    } catch (error) {
      console.error('Error loading user info:', error);
      this.userName = 'Guest';
    }
  }
}