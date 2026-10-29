package com.microservices.order.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @PostMapping("/product-service/v1/products/{id}/reduce-stock")
    Boolean reduceStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);
}
