import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../model/Product';
import { BASE_URL } from '../app.token';
import { PaginatedResponse } from '../model/PaginatedResponse';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private baseUrl: string;

  constructor(private http: HttpClient, @Inject(BASE_URL) baseUrl: string) {
    this.baseUrl = baseUrl + '/product-service/v1/products';
  }

  // Get all products
  getProducts(
    page: number,
    size: number
  ): Observable<PaginatedResponse<Product>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PaginatedResponse<Product>>(this.baseUrl, { params });
  }

  // Get a single product by ID
  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/${id}`);
  }

  // Create a new product
  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.baseUrl, product);
  }

  // Update an existing product
  updateProduct(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.baseUrl}/${id}`, product);
  }

  // Delete a product by ID
  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
