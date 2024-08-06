package com.example.ecommerce.mapper;

import lombok.NonNull;

import java.util.List;

public interface Mapper<E, D> {

    D mapToDto(@NonNull E entity);

    default List<D> mapToDtoList(List<E> list, Mapper<E, D> mapper) {
        return list.stream().map(this::mapToDto).toList();
    }

}
