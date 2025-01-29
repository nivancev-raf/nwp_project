import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { User } from '../models/user';
import { CreateUser } from '../models/create-user';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly API_URL = 'http://localhost:8080';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  async getAllUsers(): Promise<User[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });

    try {
      const response = await this.http.get<User[]>(
        `${this.API_URL}/api/users`,
        { headers }
      ).toPromise();
      
      return response || [];
    } catch (error) {
      console.error('Error fetching users:', error);
      return [];
    }
  }

  async createUser(user: CreateUser): Promise<User> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  
    try {
      const response = await this.http.post<User>(
        `${this.API_URL}/api/users`,
        user,
        { headers }
      ).toPromise();
      
      return response!;
    } catch (error) {
      console.error('Error creating user:', error);
      throw error;
    }
  }

  async getUserById(id: number): Promise<CreateUser> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  
    try {
      const response = await this.http.get<CreateUser>(
        `${this.API_URL}/api/users/${id}`,
        { headers }
      ).toPromise();
      
      return response!;
    } catch (error) {
      console.error('Error fetching user:', error);
      throw error;
    }
  }


  // update user
  async updateUser(id: number, user: CreateUser): Promise<User> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  
    try {
      const response = await this.http.put<User>(
        `${this.API_URL}/api/users/${id}`,
        user,
        { headers }
      ).toPromise();
      
      return response!;
    } catch (error) {
      console.error('Error updating user:', error);
      throw error;
    }
  }

  // delete user
  async deleteUser(id: number): Promise<void> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  
    try {
      await this.http.delete<void>(
        `${this.API_URL}/api/users/${id}`,
        { headers }
      ).toPromise();
    } catch (error) {
      console.error('Error deleting user:', error);
      throw error;
    }
  }
}