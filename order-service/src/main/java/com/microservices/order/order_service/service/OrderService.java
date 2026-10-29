package com.microservices.order.order_service.service;

import com.microservices.order.order_service.client.CartClient;
import com.microservices.order.order_service.client.ProductClient;
import com.microservices.order.order_service.entity.Order;
import com.microservices.order.order_service.entity.OrderItem;
import com.microservices.order.order_service.exception.ResourceNotFoundException;
import com.microservices.order.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderService.class);

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ProductClient productClient;

  @Autowired
  private CartClient cartClient;

  @Transactional
  public Order placeOrder(Order order) {
    if (order.getItems() == null || order.getItems().isEmpty()) {
      throw new IllegalArgumentException("Cannot place order with no items.");
    }

    log.info("Processing order placement for userId: {} with {} items", order.getUserId(), order.getItems().size());

    // 1. Verify and reduce stock in product-service via Feign Client
    for (OrderItem item : order.getItems()) {
      try {
        log.info("Calling product-service to reduce stock for productId: {}, quantity: {}", item.getProductId(), item.getQuantity());
        Boolean reduced = productClient.reduceStock(item.getProductId(), item.getQuantity());
        if (reduced == null || !reduced) {
          throw new RuntimeException("Stock deduction failed for product ID: " + item.getProductId());
        }
      } catch (Exception e) {
        log.error("Stock verification/deduction error for productId: {}", item.getProductId(), e);
        throw new RuntimeException("Failed to verify/deduct stock for product ID: " + item.getProductId() + " (" + e.getMessage() + ")", e);
      }
    }

    // 2. Compute total amount and link items
    BigDecimal computedTotal = BigDecimal.ZERO;
    for (OrderItem item : order.getItems()) {
      item.setOrder(order);
      if (item.getPrice() != null && item.getQuantity() != null) {
        computedTotal = computedTotal.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
      }
    }

    if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
      order.setTotalAmount(computedTotal);
    }
    order.setOrderDate(LocalDateTime.now());
    order.setStatus("PLACED");

    Order savedOrder = orderRepository.save(order);
    log.info("Order saved in DB with ID: {}", savedOrder.getOrderId());

    // 3. Clear the user's cart in cart-service via Feign Client
    try {
      log.info("Calling cart-service to clear cart for userId: {}", order.getUserId());
      cartClient.clearCart(order.getUserId());
    } catch (Exception e) {
      log.warn("Warning: Could not clear cart for userId {}: {}", order.getUserId(), e.getMessage());
    }

    return savedOrder;
  }

  public List<Order> getAllOrders() {
    return orderRepository.findAll();
  }

  public Order getOrderById(Long id) {
    return orderRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found for ID: " + id));
  }

  public List<Order> getOrdersByUserId(Long userId) {
    return orderRepository.findByUserId(userId);
  }

  public boolean cancelOrder(Long id) {
    Optional<Order> orderOpt = orderRepository.findById(id);
    if (orderOpt.isPresent()) {
      Order order = orderOpt.get();
      order.setStatus("CANCELLED");
      orderRepository.save(order);
      return true;
    }
    return false;
  }

  public Page<Order> getAllOrders(Pageable pageable) {
    return orderRepository.findAll(pageable);
  }
}
