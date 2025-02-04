import { Component, OnInit} from '@angular/core';
import { OrderService } from '../../services/order.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Dish } from '../../models/dish';
import { Router } from '@angular/router';


@Component({
  selector: 'app-order-schedule',
  imports: [CommonModule, FormsModule],
  templateUrl: './order-schedule.component.html',
  styleUrl: './order-schedule.component.css'
})
export class OrderScheduleComponent implements OnInit {
  availableDishes: Dish[] = [];
  selectedDishIds: number[] = [];
  scheduledTime: string = '';
  minDateTime: string;
 
  constructor(private orderService: OrderService, private router: Router) {
    this.minDateTime = new Date().toISOString().slice(0, 16); 
    // Set minDateTime to current date and time
    // .slice(0, 16) removes seconds from the date string
  }
 
  async ngOnInit() {
    this.availableDishes = await this.orderService.getAllDishes();
  }
 
  onDishSelect(event: any, dishId: number) {
    if (event.target.checked) {
      this.selectedDishIds.push(dishId);
    } else {
      this.selectedDishIds = this.selectedDishIds.filter(id => id !== dishId); // Remove dishId from selectedDishIds
    }
  }
 
  isValidOrder(): boolean {
    return this.selectedDishIds.length > 0 && !!this.scheduledTime;
  }
 
  async scheduleOrder() {
    try {
      console.log('selectedDishIds', this.selectedDishIds);
      await this.orderService.scheduleOrder({
        dishes: this.selectedDishIds,
        scheduledTime: new Date(this.scheduledTime)
      });
      // Navigate to orders list after successful scheduling
      this.router.navigate(['/orders']);
    } catch (error) {
      console.error('Error scheduling order:', error);
    }
  }
 }