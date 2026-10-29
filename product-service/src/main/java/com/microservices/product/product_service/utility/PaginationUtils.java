package com.microservices.product.product_service.utility;

import com.microservices.product.product_service.response.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaginationUtils {

    public <T> PaginatedResponse<T> buildPaginatedResponse(Page<?> page, List<T> content) {
        PaginatedResponse<T> response = new PaginatedResponse<>();
        response.setContent(content);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        return response;
    }
}