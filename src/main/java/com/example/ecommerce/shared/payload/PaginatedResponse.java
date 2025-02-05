package com.example.ecommerce.shared.payload;

import java.util.List;

public record PaginatedResponse<D>(
        List<D> content,
        int page,
        int size,
        int totalPages,
        int totalElements,
        boolean isLast
) {}
