import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from './auth.service';
import { ErrorMessage } from '../models/error-message';

@Injectable({
  providedIn: 'root'
 })
 export class ErrorService {
  private readonly API_URL = 'http://localhost:8080';
 
  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}
 
  async getErrors(page: number = 0, size: number = 10): Promise<any> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
 
    try {
      const response = await this.http.get<any>(
        `${this.API_URL}/api/errors?page=${page}&size=${size}`,
        { headers }
      ).toPromise();
      
      return response || { content: [], totalElements: 0 }; // dodato da se izbegne greška ako response bude null
    } catch (error) {
      console.error('Error fetching errors:', error);
      return { content: [], totalElements: 0 };
    }
  }
 
  // async getErrorsByOrder(orderId: number): Promise<ErrorMessage[]> {
  //   const headers = new HttpHeaders({
  //     'Authorization': `Bearer ${this.authService.getToken()}`
  //   });
 
  //   try {
  //     return await this.http.get<ErrorMessage[]>(
  //       `${this.API_URL}/api/errors/order/${orderId}`,
  //       { headers }
  //     ).toPromise() || [];
  //   } catch (error) {
  //     console.error('Error fetching order errors:', error);
  //     return [];
  //   }
  // }
 }