import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../services/order.service';
import { Order } from '../../models/order';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrl: './order-list.component.css',
  imports: [CommonModule, FormsModule, RouterModule] 
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];
  searchParams = {
    status: [] as string[],
    dateFrom: null as Date | null,
    dateTo: null as Date | null, 
    userId: null as number | null
  };
  isAdmin = false;
  userPermissions: string[] = [];
  private pollingInterval: any; // za čuvanje reference na interval
 
  constructor(
    private orderService: OrderService,
    private authService: AuthService
  ) {}
 
  async ngOnInit() {
    await this.loadOrders();
    const userId = localStorage.getItem('userId');
    this.userPermissions = await this.authService.getUserPermissions(Number(userId));

    // Započni polling svakih 5 sekundi
    this.pollingInterval = setInterval(() => {
      this.loadOrders();
    }, 5000);
  }

  ngOnDestroy() {
    // Očisti interval kada se komponenta uništi, komponenta se uništava kada se prebaci na drugu stranicu
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
  }
 
  async loadOrders() {
    this.orders = await this.orderService.searchOrders(this.searchParams);
    console.log(this.orders.forEach(order => console.log(order)));
    this.isAdmin = await this.authService.isAdmin();
    console.log("ADMIN: " + this.isAdmin);
    
  }
 
  async search() {
    await this.loadOrders();
  }
 
  clearFilters() {
    this.searchParams = {
      status: [],
      dateFrom: null,
      dateTo: null,
      userId: null
    };
    this.loadOrders();
  }

  // public cancelOrder(orderId: number): void {
  //   this.orderService.cancelOrder(orderId);
  // }


  public async cancelOrder(orderId: number): Promise<void> {
    try {
      await this.orderService.cancelOrder(orderId);
      // Update the order status locally
      const order = this.orders.find(o => o.id === orderId);
      if (order) {
        order.status = 'CANCELED';
      }
    } catch (error) {
      console.error('Error canceling order:', error);
    }
  }

  hasPermission(permission: string): boolean {
    return this.userPermissions.includes(permission);
  }
 }