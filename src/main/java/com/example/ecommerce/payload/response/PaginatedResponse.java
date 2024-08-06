package com.example.ecommerce.payload.response;

import java.util.List;

public record PaginatedResponse<D>(
        List<D> content,
        int page,
        int size,
        int totalPages,
        int totalElements,
        boolean isLast
) {}
