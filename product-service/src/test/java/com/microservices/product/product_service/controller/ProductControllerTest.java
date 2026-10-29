package com.microservices.product.product_service.controller;

import com.microservices.product.product_service.entity.Product;
import com.microservices.product.product_service.mapper.ProductMapper;
import com.microservices.product.product_service.request.ProductRequest;
import com.microservices.product.product_service.response.PaginatedResponse;
import com.microservices.product.product_service.response.ProductResponse;
import com.microservices.product.product_service.service.ProductService;
import com.microservices.product.product_service.utility.PaginationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/** Author: Pranay Sanjay Mokal. Date: 11-05-2025 Project: Microservices */

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private PaginationUtils paginationUtils;

    @InjectMocks
    private ProductController productController;

    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {

        product = new Product();
        product.setProductId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(100.0));
        product.setStock(10);
        product.setCategory("Test Category");

        productRequest = new ProductRequest();
        productRequest.setProductName("Test Product");
        productRequest.setProductDescription("Test Description");
        productRequest.setProductPrice(BigDecimal.valueOf(100.0));
        productRequest.setStock(10);
        productRequest.setCategory("Test Category");

        productResponse = new ProductResponse();
        productResponse.setProductId(1L);
        productResponse.setProductName("Test Product");
        productResponse.setProductDescription("Test Description");
        productResponse.setProductPrice(BigDecimal.valueOf(100.0));
        productResponse.setCategory("Test Category");
    }

    @Test
    void createProduct() {
        when(productMapper.mapToProduct(productRequest)).thenReturn(product);
        when(productService.createProduct(product)).thenReturn(product);

        ResponseEntity<Product> response = productController.createProduct(productRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(productMapper, times(1)).mapToProduct(productRequest);
        verify(productService, times(1)).createProduct(product);
    }

    @Test
    void getAllProducts() {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
        List<ProductResponse> productResponses = Collections.singletonList(productResponse);
        PaginatedResponse<ProductResponse> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setContent(productResponses);

        when(productService.getAllProducts(any(PageRequest.class))).thenReturn(productPage);
        when(productMapper.mapToResponse(product)).thenReturn(productResponse);
        when(paginationUtils.buildPaginatedResponse(productPage, productResponses)).thenReturn(paginatedResponse);

        ResponseEntity<PaginatedResponse<ProductResponse>> response = productController
                .getAllProducts(PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paginatedResponse, response.getBody());
        verify(productService, times(1)).getAllProducts(any(PageRequest.class));
        verify(productMapper, times(1)).mapToResponse(product);
        verify(paginationUtils, times(1)).buildPaginatedResponse(productPage, productResponses);
    }

    @Test
    void getAllProductsFallback() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Throwable ex = new RuntimeException("Service down");

        ResponseEntity<PaginatedResponse<ProductResponse>> response = productController
                .getAllProductsFallback(pageRequest, ex);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getContent().size());
        assertEquals(0, response.getBody().getTotalElements());
    }

    @Test
    void getProductById_Success() {
        when(productService.getProductById(1L)).thenReturn(product);
        when(productMapper.mapToResponse(product)).thenReturn(productResponse);

        ResponseEntity<ProductResponse> response = productController.getProductById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productResponse, response.getBody());
        verify(productService, times(1)).getProductById(1L);
        verify(productMapper, times(1)).mapToResponse(product);
    }

    @Test
    void getProductByIdFallback_GenericException() {
        Throwable ex = new RuntimeException("Database timeout");

        ResponseEntity<ProductResponse> response = productController.getProductByIdFallback(1L, ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    void getProductByIdFallback_ResourceNotFoundException_Rethrows() {
        com.microservices.product.product_service.exception.ResourceNotFoundException ex =
                new com.microservices.product.product_service.exception.ResourceNotFoundException("Product not found with ID: 99");

        org.junit.jupiter.api.Assertions.assertThrows(
                com.microservices.product.product_service.exception.ResourceNotFoundException.class,
                () -> productController.getProductByIdFallback(99L, ex)
        );
    }
}