package com.microservices.product.product_service.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.microservices.product.product_service.entity.Product;


public interface ProductService {

    Product createProduct(Product product);

    Product getProductById(Long id);

    List<Product> getAllProducts();

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);

    Page<Product> getAllProducts(Pageable pageable);

    boolean reduceStock(Long id, Integer quantity);

}
