package com.example.ecommerce.shared.payload;

import java.util.List;

/**
 * A record representing a paginated response, containing metadata about pagination
 * and the actual content of the current page.
 *
 * @param <T> the type of content in the response
 */
public record PaginatedResponse<T>(
        List<T> content,
        int page,
        int size,
        int totalPages,
        int totalElements,
        boolean isLast
) {}
