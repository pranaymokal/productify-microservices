package com.microservices.product.product_service.response;

import java.util.List;

import lombok.Data;

@Data
public class PaginatedResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

}