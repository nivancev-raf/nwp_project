import { Component, OnInit } from '@angular/core';
import { Order } from '../../models/order';
import { OrderService } from '../../services/order.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';


@Component({
  selector: 'app-order-details',
  templateUrl: './order-details.component.html',
  styleUrl: './order-details.component.css',
  imports: [CommonModule, RouterModule]
})
export class OrderDetailsComponent implements OnInit {
  order: Order | null = null;
  private orderId: number;
  private pollingInterval: any;
  userPermissions: string[] = [];

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router
  ) {
    this.orderId = Number(this.route.snapshot.paramMap.get('id'));
  }

  async ngOnInit() {
    await this.loadOrderDetails();
    const userId = localStorage.getItem('userId');
    this.userPermissions = await this.authService.getUserPermissions(Number(userId));

    // Započni polling svakih 2 sekunde
    this.pollingInterval = setInterval(() => {
      this.loadOrderDetails();
    }, 2000);
  }

  ngOnDestroy() {
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
  }

  async loadOrderDetails() {
    try {
      this.order = await this.orderService.trackOrder(this.orderId);
    } catch (error) {
      console.error('Error loading order details:', error);
      this.router.navigate(['/orders']);
    }
  }

  // this function is used to check if the order status has passed the given status
  isStatusPassed(status: string): boolean {
    const statusOrder = ['ORDERED', 'PREPARING', 'IN_DELIVERY', 'DELIVERED'];
    const currentIndex = statusOrder.indexOf(this.order?.status || '');
    const statusIndex = statusOrder.indexOf(status);
    return currentIndex > statusIndex;
  }

  hasPermission(permission: string): boolean {
    return this.userPermissions.includes(permission);
  }
}