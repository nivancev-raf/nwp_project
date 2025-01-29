// auth.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { LoginResponse } from '../models/login-response';	

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080';
  private currentUserId: number | null = null;

  private isLoggedInSubject = new BehaviorSubject<boolean>(this.tokenExists());
  isLoggedIn$ = this.isLoggedInSubject.asObservable(); // $ at the end of the variable name is a convention for observables

  private userPermissionsSubject = new BehaviorSubject<string[]>([]);
  userPermissions$ = this.userPermissionsSubject.asObservable();

  constructor(private http: HttpClient) {}

  tokenExists(): boolean {
    if (typeof localStorage === 'undefined') {
      console.log('localStorage is not available');
      return false;
    }
    return !!localStorage.getItem('token'); // !! converts to boolean, null -> false, string -> true
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  async getUserPermissions(userId: number): Promise<string[]> {
    try {
      const headers = {
        'Authorization': `Bearer ${this.getToken()}`
      };

      const permissions = await this.http.get<string[]>(
        `${this.API_URL}/auth/permissions/${userId}`,
        { headers }
      ).toPromise();

      return permissions || [];
    } catch (error) {
      console.error('Error fetching permissions:', error);
      return [];
    }
  }

  async login(email: string, password: string): Promise<void> {
    try {
      const response = await this.http.post<LoginResponse>(
        `${this.API_URL}/auth/login`,
        { email, password },
        { 
          headers: new HttpHeaders({
            'Content-Type': 'application/json'
          })
        }
      ).toPromise();

      if (response && response.token) {
        localStorage.setItem('token', response.token);
        this.isLoggedInSubject.next(true);
      } else {
        throw new Error('Token not received');
      }
    } catch (error) {
      if (error instanceof HttpErrorResponse) {
        if (error.status === 401) {
          alert('Invalid credentials');
          throw new Error('Invalid credentials');
        } else if (error.status === 0) {
          alert('Backend is down');
          throw new Error('Backend is down');
        } else {
          throw new Error('An unknown error occurred');
        }
      } else {
        throw new Error('An unknown error occurred');
      }
    }
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('permissions');
    localStorage.removeItem('userId');
    this.isLoggedInSubject.next(false);
    this.userPermissionsSubject.next([]); // Resetujemo permisije pri logout-u
  }


  // fetch current user's name
  async getCurrentUser(): Promise<any> {
    try {
      const headers = {
        'Authorization': `Bearer ${this.getToken()}`
      };
  
      const response = await this.http.get<any>(
        `${this.API_URL}/auth/current-user`,
        { headers }
      ).toPromise();

      if (response && response.id) {
        this.setCurrentUserId(response.id);
      }

      if (response && response.permissions) {
        this.userPermissionsSubject.next(response.permissions);
      }
  
      return response;
    } catch (error) {
      console.error('Error fetching current user:', error);
      throw error;
    }
  }

  setCurrentUserId(id: number) {
    this.currentUserId = id;
    localStorage.setItem('userId', id.toString());
  }

  getCurrentUserId(): number | null {
    if (this.currentUserId) return this.currentUserId;
    const savedId = localStorage.getItem('userId');
    return savedId ? parseInt(savedId) : null;
  }
}
