package com.microservices.cart.cart_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/** Author: Pranay Sanjay Mokal. Date: 10-05-2025 Project: Microservices */

@Document(collection = "carts")
@Data
public class Cart {
    @Id
    private String id;
    private String userId;
    private List<CartItem> items = new ArrayList<>();
}