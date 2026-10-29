package com.microservices.cart.cart_service.service;

import com.microservices.cart.cart_service.exception.ResourceNotFoundException;
import com.microservices.cart.cart_service.model.Cart;
import com.microservices.cart.cart_service.model.CartItem;
import com.microservices.cart.cart_service.respository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Author: Pranay Sanjay Mokal. Date: 10-05-2025 Project: Microservices */

@Service
public class CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart addItemToCart(String userId, CartItem item) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setItems(new java.util.ArrayList<>());
            return newCart;
        });

        if (cart.getItems() == null) {
            cart.setItems(new java.util.ArrayList<>());
        }

        boolean itemExists = false;
        for (CartItem existingItem : cart.getItems()) {
            if (existingItem.getProductId() != null && existingItem.getProductId().equals(item.getProductId())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                existingItem.setPrice(item.getPrice());
                itemExists = true;
                break;
            }
        }
        if (!itemExists) {
            cart.getItems().add(item);
        }

        return cartRepository.save(cart);
    }

    public Cart removeItemFromCart(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart not found for userId: " + userId));
        if (cart.getItems() != null) {
            cart.getItems().removeIf(cartItem -> cartItem.getProductId() != null && cartItem.getProductId().equals(productId));
        }
        return cartRepository.save(cart);
    }

    public Cart getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setItems(new java.util.ArrayList<>());
                    return newCart;
                });
    }

    public void clearCart(String userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            if (cart.getItems() != null) {
                cart.getItems().clear();
                cartRepository.save(cart);
            }
        });
    }
}
