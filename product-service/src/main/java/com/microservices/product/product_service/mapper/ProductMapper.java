package com.microservices.product.product_service.mapper;

import com.microservices.product.product_service.entity.Product;
import com.microservices.product.product_service.request.ProductRequest;
import com.microservices.product.product_service.response.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product mapToProduct(ProductRequest request){
        Product product=new Product();
        product.setName(request.getProductName());
        product.setDescription(request.getProductDescription());
        product.setPrice(request.getProductPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        return product;
    }

    public ProductResponse mapToResponse(Product product){
        ProductResponse response=new ProductResponse();
        response.setProductId(product.getProductId());
        response.setProductName(product.getName());
        response.setProductPrice(product.getPrice());
        response.setCategory(product.getCategory());
        response.setProductDescription(product.getDescription());
        return response;
    }
}
