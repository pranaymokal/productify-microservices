import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { TextareaModule} from 'primeng/textarea';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DropdownModule } from 'primeng/dropdown';
import { ProductService } from '../service/product.service';

import { Router } from '@angular/router';

@Component({
  selector: 'app-add-product',
  standalone: true,
  imports: [ReactiveFormsModule, InputTextModule, ButtonModule, CardModule, TextareaModule, DropdownModule],
  templateUrl: './add-product.component.html',
  styleUrl: './add-product.component.css'
})
export class AddProductComponent {
  
  addProductForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private router: Router
  ) {
    this.addProductForm = this.fb.group({
      productName: ['', Validators.required],
      productPrice: ['', [Validators.required, Validators.min(0.01)]],
      productDescription: [''],
      stock: ['', [Validators.required, Validators.min(1)]],
      category: ['', Validators.required],
    });
  }

  onSubmit() {
    if (!this.addProductForm.valid) {
      alert('Please fill in all required fields correctly.');
      return;
    }
    this.productService.createProduct(this.addProductForm.value).subscribe({
      next: (response) => {
        alert('Product "' + response.productName + '" created successfully!');
        this.addProductForm.reset();
        this.router.navigate(['/products']);
      },
      error: (error) => {
        console.error('Error creating product:', error);
        alert('Failed to create product: ' + (error?.error?.message || error?.message || 'Please check API Gateway connection.'));
      }
    });
  }
}
