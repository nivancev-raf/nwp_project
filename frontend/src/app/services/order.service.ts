// order.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { AuthService } from './auth.service';
import { Order } from '../models/order';
import { Dish } from '../models/dish';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private readonly API_URL = 'http://localhost:8080';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  async getAllDishes(): Promise<Dish[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });

    try {
      const response = await this.http.get<Dish[]>(
        `${this.API_URL}/api/dishes`,
        { headers }
      ).toPromise();
      
      return response || [];
    } catch (error) {
      console.error('Error fetching dishes:', error);
      return [];
    }
  }

  async createOrder(dishIds: number[]): Promise<Order> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`,
      'Content-Type': 'application/json'  // dodato za eksplicitno definisanje tipa sadržaja
    });

    const requestBody = {
      dishes: dishIds  // promenjen key iz 'dishIds' u 'dishes' da odgovara JSON-u iz slike
    };

    try {
      const response = await this.http.post<Order>(
        `${this.API_URL}/api/orders`,
        requestBody,
        { headers }
      ).toPromise();
      
      return response!;
    } catch (error) {
      console.error('Error creating order:', error);
      throw error;
    }
  }

  async cancelOrder(id: number): Promise<void> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });

    console.log('cancelOrder', id);

    try {
      await this.http.delete<void>(
        `${this.API_URL}/api/orders/${id}`,
        { headers }
      ).toPromise();
    } catch (error) {
      console.error('Error canceling order:', error);
      throw error;
    }
  }


  // async searchOrders(params: any): Promise<Order[]> {
  //   const headers = new HttpHeaders({
  //     'Authorization': `Bearer ${this.authService.getToken()}`
  //   });
  
  //   console.log(params);
  //   const queryParams = new HttpParams()
  //     .set('status', params.status.join(','))
  //     .set('dateFrom', params.dateFrom ? params.dateFrom : '')
  //     .set('dateTo', params.dateTo ? params.dateTo : '')
  //     .set('userId', params.userId?.toString() || '');
  
  //   try {
  //     const response = await this.http.get<Order[]>(
  //       `${this.API_URL}/api/orders`,
  //       { headers, params: queryParams }
  //     ).toPromise();
      
  //     return response || [];
  //   } catch (error) {
  //     console.error('Error searching orders:', error);
  //     return [];
  //   }
  // }

  async searchOrders(params: any): Promise<Order[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  
    let queryParams = new HttpParams();
  
    if (params.status && params.status.length > 0) {
      queryParams = queryParams.set('status', params.status.join(','));
    }
    if (params.dateFrom) {
      queryParams = queryParams.set('dateFrom', params.dateFrom instanceof Date ? params.dateFrom.toISOString() : new Date(params.dateFrom).toISOString());
    }
    if (params.dateTo) {
      queryParams = queryParams.set('dateTo', params.dateTo instanceof Date ? params.dateTo.toISOString() : new Date(params.dateTo).toISOString());
    }
    if (params.userId) {
      queryParams = queryParams.set('userId', params.userId.toString());
    }
  
    try {
      const response = await this.http.get<Order[]>(
        `${this.API_URL}/api/orders`,
        { headers, params: queryParams }
      ).toPromise();
      
      return response || [];
    } catch (error) {
      console.error('Error searching orders:', error);
      return [];
    }
  }

  // order.service.ts
async trackOrder(id: number): Promise<Order> {
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${this.authService.getToken()}`
  });

  try {
    const response = await this.http.get<Order>(
      `${this.API_URL}/api/orders/track/${id}`,
      { headers }
    ).toPromise();
    
    return response!;
  } catch (error) {
    console.error('Error tracking order:', error);
    throw error;
  }
}
}