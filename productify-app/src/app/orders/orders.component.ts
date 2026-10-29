import { Component, OnInit } from '@angular/core';
import { TableModule } from 'primeng/table';
import { OrderService } from '../service/order.service';
import { Order } from '../model/Order';

import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-orders',
  imports: [TableModule, CommonModule],
  standalone: true,
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css',
})
export class OrdersComponent implements OnInit {
  orders: Order[] = []; 

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  // Fetch orders from the OrderService
  loadOrders(): void {
    this.orderService.getAllOrders().subscribe({
      next: (data) => {
        this.orders = data;
      },
      error: (err) => {
        console.error('Error fetching orders:', err);
      },
    });
  }
}