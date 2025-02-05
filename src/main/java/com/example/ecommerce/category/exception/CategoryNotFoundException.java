package com.example.ecommerce.category.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException(Long categoryId) {
        super(ErrorMessages.CATEGORY_NOT_FOUND.message(categoryId));
    }

}
