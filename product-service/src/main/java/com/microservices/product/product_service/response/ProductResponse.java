package com.microservices.product.product_service.response;

import lombok.Data;

import java.math.BigDecimal;

/** Author: Pranay Sanjay Mokal. Date: 02-05-2025 Project: Microservices */

@Data
public class ProductResponse {

    private Long productId;

    private String productName;

    private String productDescription;

    private BigDecimal productPrice;

    private String category;

}
