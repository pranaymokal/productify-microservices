import { Component, OnInit } from '@angular/core';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PanelModule } from 'primeng/panel';
import { ListboxModule } from 'primeng/listbox';
import { ProductService } from '../service/product.service';
import { Product } from '../model/Product';
import { PaginatedResponse } from '../model/PaginatedResponse';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [TableModule, ButtonModule, CommonModule, FormsModule, PanelModule, ListboxModule],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css',
})
export class ProductListComponent implements OnInit {
getTotalPages() {
throw new Error('Method not implemented.');
}
  selectedProduct: Product | null = null;
  products: Product[] = [];
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

  addToCart(product: Product): void {
    console.log('Product added to cart:', product);
  }

  onPageChange(newPage: number): void {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.page = newPage;
      this.loadProducts();
    }
  }
}