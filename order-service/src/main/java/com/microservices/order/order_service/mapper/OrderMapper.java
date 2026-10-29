package com.microservices.order.order_service.mapper;

import org.springframework.stereotype.Component;

import com.microservices.order.order_service.entity.Order;
import com.microservices.order.order_service.entity.OrderItem;
import com.microservices.order.order_service.request.OrderItemRequest;
import com.microservices.order.order_service.request.OrderRequest;
import com.microservices.order.order_service.response.OrderItemResponse;
import com.microservices.order.order_service.response.OrderResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public Order mapToOrder(OrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setTotalAmount(request.getTotalAmount());
        order.setOrderDate(request.getOrderDate());
        order.setStatus(request.getStatus());

        if (request.getItems() != null) {
            for (OrderItemRequest itemReq : request.getItems()) {
                OrderItem item = new OrderItem();
                item.setProductId(itemReq.getProductId());
                item.setQuantity(itemReq.getQuantity());
                item.setPrice(itemReq.getPrice());
                order.addItem(item);
            }
        }
        return order;
    }

    public OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setUserId(order.getUserId());
        response.setTotalAmount(order.getTotalAmount());
        response.setOrderDate(order.getOrderDate());
        response.setStatus(order.getStatus());

        if (order.getItems() != null) {
            List<OrderItemResponse> itemResponses = order.getItems().stream()
                    .map(item -> {
                        OrderItemResponse ir = new OrderItemResponse();
                        ir.setId(item.getId());
                        ir.setProductId(item.getProductId());
                        ir.setQuantity(item.getQuantity());
                        ir.setPrice(item.getPrice());
                        return ir;
                    })
                    .collect(Collectors.toList());
            response.setItems(itemResponses);
        } else {
            response.setItems(new ArrayList<>());
        }
        return response;
    }
}
