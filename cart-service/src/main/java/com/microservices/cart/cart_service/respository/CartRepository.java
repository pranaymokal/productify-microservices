package com.microservices.cart.cart_service.respository;

/** Author: Pranay Sanjay Mokal. Date: 10-05-2025 Project: Microservices */

import com.microservices.cart.cart_service.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);
}