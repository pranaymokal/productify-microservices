package com.microservices.product.product_service.controller;

import com.microservices.product.product_service.entity.Product;
import com.microservices.product.product_service.mapper.ProductMapper;
import com.microservices.product.product_service.request.ProductRequest;
import com.microservices.product.product_service.response.PaginatedResponse;
import com.microservices.product.product_service.response.ProductResponse;
import com.microservices.product.product_service.service.ProductService;
import com.microservices.product.product_service.utility.PaginationUtils;

import com.microservices.product.product_service.exception.ResourceNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** Author: Pranay Sanjay Mokal Date: 02-05-2025 Project: Microservices */

@RestController
@RequestMapping("product-service/v1/products")
public class ProductController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PaginationUtils paginationUtils;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductRequest product) {
        Product newProduct = productService.createProduct(productMapper.mapToProduct(product));
        return new ResponseEntity<Product>(newProduct, HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @CircuitBreaker(name = "productService", fallbackMethod = "getAllProductsFallback")
    public ResponseEntity<PaginatedResponse<ProductResponse>> getAllProducts(Pageable pageable) {
        Page<Product> products = productService.getAllProducts(pageable);
        List<ProductResponse> productResponses = products.stream()
                .map(productMapper::mapToResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(paginationUtils.buildPaginatedResponse(products, productResponses), HttpStatus.OK);
    }

    public ResponseEntity<PaginatedResponse<ProductResponse>> getAllProductsFallback(Pageable pageable, Throwable throwable) {
        log.error("Circuit breaker fallback triggered for getAllProducts. Reason: {}", throwable.getMessage());
        PaginatedResponse<ProductResponse> fallbackResponse = new PaginatedResponse<>();
        fallbackResponse.setContent(Collections.emptyList());
        fallbackResponse.setPageNumber(pageable != null ? pageable.getPageNumber() : 0);
        fallbackResponse.setPageSize(pageable != null ? pageable.getPageSize() : 10);
        fallbackResponse.setTotalElements(0);
        fallbackResponse.setTotalPages(0);
        return ResponseEntity.ok(fallbackResponse);
    }

    @GetMapping("/{id}")
    @Retry(name = "productService", fallbackMethod = "getProductByIdFallback")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(productMapper.mapToResponse(product));
    }

    public ResponseEntity<ProductResponse> getProductByIdFallback(Long id, Throwable throwable) {
        if (throwable instanceof ResourceNotFoundException) {
            throw (ResourceNotFoundException) throwable;
        }
        log.error("All retry attempts exhausted for getProductById with ID: {}. Reason: {}", id, throwable.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductRequest productRequest) {
        Product updated = productService.updateProduct(id, productMapper.mapToProduct(productRequest));
        return ResponseEntity.ok(productMapper.mapToResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reduce-stock")
    public ResponseEntity<Boolean> reduceStock(@PathVariable Long id, @RequestParam Integer quantity) {
        log.info("Received request to reduce stock for productId: {}, quantity: {}", id, quantity);
        boolean result = productService.reduceStock(id, quantity);
        log.info("Stock reduction completed for productId: {}, result: {}", id, result);
        return ResponseEntity.ok(result);
    }
}
