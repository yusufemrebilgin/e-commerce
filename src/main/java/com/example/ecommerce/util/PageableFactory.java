package com.example.ecommerce.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageableFactory {

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIR = "asc";

    private PageableFactory() {}

    public static Pageable getPageable(int page, int size) {
        return PageRequest.of(page, size);
    }

    public static Pageable getPageable(int page, int size, String sortFormat) {
        return PageRequest.of(page, size, parseSort(sortFormat));
    }

    private static Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.fromString(DEFAULT_SORT_DIR), DEFAULT_SORT_BY);
        }

        // TODO: Birden fazla alan ile sıralama için düzenleme yapılabilir

        String[] sortProperties = sort.split(":");
        if (sortProperties.length != 2) {
            throw new IllegalArgumentException("Invalid sort parameter format (Expected field:direction)");
        }

        String sortBy = sortProperties[0], sortDir = sortProperties[1];
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        return Sort.by(direction, sortBy);
    }

}
