import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Order } from '../model/Order';
import { BASE_URL } from '../app.token';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private baseUrl: string;

  constructor(private http: HttpClient, @Inject(BASE_URL) baseUrl: string) {
    this.baseUrl = baseUrl + '/order-service/v1/orders';
  }

  // Get all orders
  getAllOrders(): Observable<Order[]> {
    return this.http.get<any>(this.baseUrl).pipe(
      map((response) => {
        if (response && response.content) {
          return response.content;
        }
        return Array.isArray(response) ? response : [];
      })
    );
  }

  // Get a single order by ID
  getOrderById(orderId: string): Observable<Order> {
    return this.http.get<Order>(`${this.baseUrl}/${orderId}`);
  }

  // Create a new order
  createOrder(order: Order): Observable<Order> {
    return this.http.post<Order>(this.baseUrl, order);
  }

  // Update an existing order
  updateOrder(orderId: string, order: Order): Observable<Order> {
    return this.http.put<Order>(`${this.baseUrl}/${orderId}`, order);
  }

  // Delete an order by ID
  deleteOrder(orderId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${orderId}`);
  }
}