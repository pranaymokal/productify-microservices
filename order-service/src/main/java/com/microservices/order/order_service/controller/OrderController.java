package com.microservices.order.order_service.controller;

import com.microservices.order.order_service.entity.Order;
import com.microservices.order.order_service.mapper.OrderMapper;
import com.microservices.order.order_service.request.OrderRequest;
import com.microservices.order.order_service.response.OrderResponse;
import com.microservices.order.order_service.response.PaginatedResponse;
import com.microservices.order.order_service.service.OrderService;
import com.microservices.order.order_service.utility.PaginationUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/** Author : pranay Date : 02-05-2025 Project: Microservices */

@RestController
@RequestMapping("order-service/v1/orders")
public class OrderController {

  @Autowired
  private OrderService orderService;

  @Autowired
  private OrderMapper orderMapper;

  @Autowired
  private PaginationUtils paginationUtils;

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderController.class);

  @PostMapping
  public ResponseEntity<OrderResponse> placeOrder(@RequestBody @Valid OrderRequest orderRequest) {
    log.info("Received request to place order for userId: {}", orderRequest.getUserId());
    Order order = orderMapper.mapToOrder(orderRequest);
    Order savedOrder = orderService.placeOrder(order);
    log.info("Order placed successfully with ID: {}", savedOrder.getOrderId());
    return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.mapToResponse(savedOrder));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PaginatedResponse<OrderResponse>> getAllOrders(Pageable pageable) {
    Page<Order> orders = orderService.getAllOrders(pageable);

    List<OrderResponse> orderResponses = orders.stream()
        .map(orderMapper::mapToResponse)
        .collect(Collectors.toList());

    PaginatedResponse<OrderResponse> response = paginationUtils.buildPaginatedResponse(orders, orderResponses);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
    Order order = orderService.getOrderById(id);
    return new ResponseEntity<>(orderMapper.mapToResponse(order), HttpStatus.OK);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable Long userId) {
    List<Order> orders = orderService.getOrdersByUserId(userId);
    List<OrderResponse> responses = orders.stream()
        .map(orderMapper::mapToResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(responses);
  }

  @PutMapping("/{id}/cancel")
  public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
    boolean cancelled = orderService.cancelOrder(id);
    return cancelled ? ResponseEntity.ok("Order cancelled") : ResponseEntity.notFound().build();
  }
}
