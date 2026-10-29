package com.microservices.cart.cart_service.controller;

import com.microservices.cart.cart_service.model.Cart;
import com.microservices.cart.cart_service.model.CartItem;
import com.microservices.cart.cart_service.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cart-service/v1/carts")
public class CartController {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CartController.class);

  @Autowired private CartService cartService;

  @PostMapping("/{userId}/items")
  public ResponseEntity<Cart> addItem(@PathVariable String userId, @RequestBody CartItem item) {
    log.info("Received request to add item to cart for userId: {}", userId);
    Cart updatedCart = cartService.addItemToCart(userId, item);
    return ResponseEntity.ok(updatedCart);
  }

  @DeleteMapping("/{userId}/items/{productId}")
  public ResponseEntity<Cart> removeItem(
      @PathVariable String userId, @PathVariable String productId) {
    log.info("Received request to remove item productId: {} from cart for userId: {}", productId, userId);
    Cart updatedCart = cartService.removeItemFromCart(userId, productId);
    return ResponseEntity.ok(updatedCart);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<Cart> getCart(@PathVariable String userId) {
    log.info("Received request to get cart for userId: {}", userId);
    Cart cart = cartService.getCartByUserId(userId);
    return ResponseEntity.ok(cart);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> clearCart(@PathVariable String userId) {
    log.info("Received request to clear cart for userId: {}", userId);
    cartService.clearCart(userId);
    log.info("Cart cleared successfully for userId: {}", userId);
    return ResponseEntity.noContent().build();
  }

}
