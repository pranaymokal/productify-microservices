package com.microservices.product.product_service.implementation;

import com.microservices.product.product_service.config.RedisConfig;
import com.microservices.product.product_service.entity.Product;
import com.microservices.product.product_service.exception.ResourceNotFoundException;
import com.microservices.product.product_service.repository.ProductRepository;
import com.microservices.product.product_service.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductServiceImplementation implements ProductService {

    @Autowired
    private ProductRepository repository;

    @Override
    @CacheEvict(value = RedisConfig.ALL_PRODUCTS_CACHE, allEntries = true)
    public Product createProduct(Product product) {
        log.info("Creating product: {}", product.getName());
        return repository.save(product);
    }

    @Override
    @Cacheable(value = RedisConfig.PRODUCT_CACHE, key = "#id")
    public Product getProductById(Long id) {
        log.info("Fetching product with ID {} from database (cache miss)", id);
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    @Override
    @Cacheable(value = RedisConfig.ALL_PRODUCTS_CACHE)
    public List<Product> getAllProducts() {
        log.info("Fetching all products from database (cache miss)");
        return repository.findAll();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = RedisConfig.PRODUCT_CACHE, key = "#id"),
            @CacheEvict(value = RedisConfig.ALL_PRODUCTS_CACHE, allEntries = true)
    })
    public Product updateProduct(Long id, Product product) {
        log.info("Updating product with ID {} and evicting cache", id);
        Product existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());
        existing.setCategory(product.getCategory());
        return repository.save(existing);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = RedisConfig.PRODUCT_CACHE, key = "#id"),
            @CacheEvict(value = RedisConfig.ALL_PRODUCTS_CACHE, allEntries = true)
    })
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID {} and evicting cache", id);
        repository.deleteById(id);
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = RedisConfig.PRODUCT_CACHE, key = "#id"),
            @CacheEvict(value = RedisConfig.ALL_PRODUCTS_CACHE, allEntries = true)
    })
    public boolean reduceStock(Long id, Integer quantity) {
        log.info("Reducing stock for product ID {} by {} and evicting cache", id, quantity);
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock for product id " + id + ". Available: " + product.getStock() + ", requested: " + quantity);
        }
        product.setStock(product.getStock() - quantity);
        repository.save(product);
        return true;
    }
}
