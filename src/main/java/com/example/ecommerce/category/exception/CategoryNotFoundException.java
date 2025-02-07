package com.example.ecommerce.category.exception;

import com.example.ecommerce.shared.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException(Long categoryId) {
        super("Category not found with id " + categoryId);
    }

}
