package com.example.ecommerce.mapper;

import com.example.ecommerce.payload.response.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public final class PaginationMapper {

    public <E, D> PaginatedResponse<D> toPaginatedResponse(Page<E> page, Mapper<E, D> mapper) {
        return new PaginatedResponse<>(
                page.getContent().stream().map(mapper::mapToDto).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getNumberOfElements(),
                page.isLast()
        );
    }

}
