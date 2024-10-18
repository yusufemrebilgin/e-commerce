package com.example.ecommerce.exception.product;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.NotFoundException;

import java.util.UUID;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException(UUID productId) {
        super(ErrorMessages.PRODUCT_NOT_FOUND.message(productId));
    }

}
