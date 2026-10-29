package com.microservices.cart.cart_service.service;

import com.microservices.cart.cart_service.model.Cart;
import com.microservices.cart.cart_service.model.CartItem;
import com.microservices.cart.cart_service.respository.CartRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private Cart cart;

    private CartItem item;

    @BeforeEach
    void setUp() {
    cart = new Cart();
    cart.setUserId("user1");
    cart.setItems(new ArrayList<>()); // <-- Add this line
    item = new CartItem();
    item.setProductId("prod1");
    }

    @Test
    void addItemToCart() {
        when(cartRepository.findByUserId("user1")).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);
        Cart result = cartService.addItemToCart("user1", item);
        assertEquals(cart, result);
        verify(cartRepository, times(1)).save(cart);
    }
}