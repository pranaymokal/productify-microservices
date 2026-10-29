package com.microservices.order.order_service.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class OrderResponse {

  private Long orderId;

  private Long userId;

  private BigDecimal totalAmount;

  private LocalDateTime orderDate;

  private String status;

  private List<OrderItemResponse> items;
}
