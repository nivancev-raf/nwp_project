import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { User } from '../../models/user';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-user-list',
  imports: [CommonModule, RouterModule],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css'
})
export class UserListComponent implements OnInit {
  users: User[] = [];
  loading = false;
  error = '';
  userPermissions: string[] = [];

  constructor(
    private userService: UserService,
    private authService: AuthService
  
  ) {}

  async ngOnInit() {
    try {
      this.loading = true;
      this.users = await this.userService.getAllUsers();
      // need to get current logged in user permissions (I have saved userId in localStorage)
      const userId = localStorage.getItem('userId');
      if (userId !== null) {
        this.authService.getUserPermissions(Number(userId))
        .then(permissions => {
          this.userPermissions = permissions;
          if (this.userPermissions.length === 0) {
            alert('Upozorenje: Nemate dodeljene permisije.');
          }
        })
        .catch(error => {
          console.error('Error fetching permissions:', error);
        });
      }

    } catch (error) {
      this.error = 'Error loading users';
      console.error('Error:', error);
    } finally {
      this.loading = false;
    }
  }

  async deleteUser(userId: number) {
    try {
      await this.userService.deleteUser(userId);
      this.users = this.users.filter(u => u.id !== userId);
    } catch (error) {
      console.error('Error deleting user:', error);
    }
  }
}