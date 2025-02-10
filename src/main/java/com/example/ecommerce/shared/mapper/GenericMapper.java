package com.example.ecommerce.shared.mapper;

import com.example.ecommerce.shared.payload.PaginatedResponse;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic mapper interface for entity-to-response conversion.
 *
 * @param <E> Entity type
 * @param <R> Response type
 */
public interface GenericMapper<E, R> {

    R mapToResponse(E entity);

    /**
     * Converts a list of entities to a list of responses.
     */
    default List<R> mapToResponseList(List<E> entityList) {
        return entityList.stream().map(this::mapToResponse).toList();
    }

    /**
     * Converts a {@link Page} of entities to a {@link PaginatedResponse}.
     *
     * @param page the page of entities to convert
     * @return a {@link PaginatedResponse} containing the responses and pagination info
     */
    default PaginatedResponse<R> mapToPaginatedResponse(Page<E> page) {
        return new PaginatedResponse<>(
                page.getContent().stream().map(this::mapToResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getNumberOfElements(),
                page.isLast()
        );
    }

}
