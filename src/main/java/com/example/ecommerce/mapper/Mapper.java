package com.example.ecommerce.mapper;

import lombok.NonNull;

import java.util.List;

public interface Mapper<E, R> {

    R mapToResponse(@NonNull E entity);

    default List<R> mapToResponseList(List<E> list, Mapper<E, R> mapper) {
        return list.stream().map(mapper::mapToResponse).toList();
    }

}
