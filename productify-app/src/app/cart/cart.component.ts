import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Product } from '../model/Product';
import { ProductService } from '../service/product.service';
import { PaginatedResponse } from '../model/PaginatedResponse';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css',
})
export class CartComponent implements OnInit {
  products: Product[] = [];
  selectedProduct: Product | null = null;
  totalElements: number = 0;
  totalPages: number = 0; 
  page: number = 0;
  size: number = 10;

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  // Load products from the ProductService
  loadProducts(): void {
    this.productService.getProducts(this.page, this.size).subscribe((data: PaginatedResponse<Product>) => {
          this.products = data.content;
          this.totalElements = data.totalElements;
        });
  }

  selectProduct(product: Product): void {
    this.selectedProduct = product;
  }

  updateSelection(): void {
    console.log('Selection updated:', this.products);
  }

  placeOrder(): void {
    if (this.selectedProduct) {
      console.log('Order placed for:', this.selectedProduct);
    } else {
      console.log('No product selected for the order.');
    }
  }
}