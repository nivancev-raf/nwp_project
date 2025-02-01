// order-create.component.ts
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { Dish } from '../../models/dish';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-order-create',
  templateUrl: './order-create.component.html',
  styleUrls: ['./order-create.component.css'],
  imports: [CommonModule]
})
export class OrderCreateComponent implements OnInit {
  dishes: Dish[] = [];
  selectedDishIds: number[] = [];
  errorMessage: string = '';

  constructor(
    private orderService: OrderService,
    private router: Router
  ) {}

  async ngOnInit() {
    try {
      this.dishes = await this.orderService.getAllDishes();
    } catch (error) {
      this.errorMessage = 'Error loading dishes';
    }
  }

  onDishSelection(dishId: number, event: any) {
    if (event.target.checked) {
      this.selectedDishIds.push(dishId);
    } else {
      this.selectedDishIds = this.selectedDishIds.filter(id => id !== dishId);
    }
  }

  async createOrder() {
    if (this.selectedDishIds.length === 0) {
      this.errorMessage = 'Please select at least one dish';
      return;
    }

    try {
      await this.orderService.createOrder(this.selectedDishIds);
      this.router.navigate(['/orders']); 
    } catch (error: any) {
      this.errorMessage = error.message || 'Error creating order';
    }
  }
}