import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  standalone: true,
  imports: [RouterModule, CommonModule]
})
export class NavbarComponent implements OnInit {
  isLoggedIn = false;
  userPermissions: string[] = [];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    // Check login status changes
    this.authService.isLoggedIn$.subscribe(
      (loggedIn) => {
        this.isLoggedIn = loggedIn;
      }
    );

      // Pratimo permisije
      this.authService.userPermissions$.subscribe(
        (permissions) => {
          this.userPermissions = permissions;
        }
      );
    }
  
  ngOnInit() {
    // Check initial state on page load/refresh
    if (this.authService.tokenExists()) {
      this.isLoggedIn = true;
      this.loadUserPermissions();
    }
  }

  private async loadUserPermissions() {
    const userId = this.authService.getCurrentUserId();
    if (userId !== null) {
      try {
        this.userPermissions = await this.authService.getUserPermissions(userId);
        if (this.userPermissions.length === 0) {
          alert('Upozorenje: Nemate dodeljene permisije.');
        }
      } catch (error) {
        console.error('Error fetching permissions:', error);
      }
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}