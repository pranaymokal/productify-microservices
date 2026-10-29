package com.microservices.order.order_service.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {

    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}
