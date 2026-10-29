package com.microservices.cart.cart_service.model;
/**
* Author: Pranay Sanjay Mokal.
* Date: 10-05-2025
* Project: Microservices
**/
import lombok.Data;

@Data
public class CartItem {
    private String productId;
    private int quantity;
    private double price;
}