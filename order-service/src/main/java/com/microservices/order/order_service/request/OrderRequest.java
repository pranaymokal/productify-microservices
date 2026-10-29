package com.microservices.order.order_service.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private BigDecimal totalAmount;

    private LocalDateTime orderDate;

    private String status;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;
}
