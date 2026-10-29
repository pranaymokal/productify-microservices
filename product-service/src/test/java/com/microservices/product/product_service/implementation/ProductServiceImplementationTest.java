package com.microservices.product.product_service.implementation;

import com.microservices.product.product_service.entity.Product;
import com.microservices.product.product_service.exception.ResourceNotFoundException;
import com.microservices.product.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplementationTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImplementation productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(100.0));
        product.setStock(10);
        product.setCategory("Test Category");
    }

    @Test
    void createProduct() {
        when(repository.save(product)).thenReturn(product);
        Product createdProduct = productService.createProduct(product);
        assertNotNull(createdProduct);
        assertEquals(product.getProductId(), createdProduct.getProductId());
        verify(repository, times(1)).save(product);
    }

    @Test
    void getProductById() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        Product foundProduct = productService.getProductById(1L);
        assertNotNull(foundProduct);
        assertEquals(product.getProductId(), foundProduct.getProductId());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void getProductById_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void getAllProducts() {
        List<Product> products = Arrays.asList(product);
        when(repository.findAll()).thenReturn(products);
        List<Product> allProducts = productService.getAllProducts();
        assertNotNull(allProducts);
        assertEquals(1, allProducts.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void updateProduct() {
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(BigDecimal.valueOf(200.0));
        updatedProduct.setStock(20);
        updatedProduct.setCategory("Updated Category");
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(any(Product.class))).thenReturn(updatedProduct);
        Product result = productService.updateProduct(1L, updatedProduct);
        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(product);
    }

    @Test
    void updateProduct_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        Product updatedProduct = new Product();
        assertThrows(RuntimeException.class, () -> productService.updateProduct(1L, updatedProduct));
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void deleteProduct() {
        doNothing().when(repository).deleteById(1L);
        productService.deleteProduct(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void getAllProductsWithPagination() {
        Page<Product> page = new PageImpl<>(List.of(product));
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(page);
        Page<Product> result = productService.getAllProducts(PageRequest.of(0, 10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository, times(1)).findAll(PageRequest.of(0, 10));
    }
}