package com.example.ecommerce.exception;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException() {
        super(String.format(DEFAULT_FORMAT, "Category"));
    }

    public CategoryNotFoundException(Long id) {
        super(String.format(DEFAULT_FORMAT_WITH_ID, "Category", id));
    }

}
