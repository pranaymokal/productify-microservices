package com.microservices.order.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CART-SERVICE")
public interface CartClient {

    @DeleteMapping("/cart-service/v1/carts/{userId}")
    void clearCart(@PathVariable("userId") Long userId);
}
