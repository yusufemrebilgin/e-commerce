package com.example.ecommerce.exception.category;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException(Long id) {
        super(ErrorMessages.CATEGORY_NOT_FOUND.format(id));
    }

}
