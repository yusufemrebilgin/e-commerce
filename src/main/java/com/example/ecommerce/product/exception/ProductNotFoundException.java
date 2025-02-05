package com.example.ecommerce.product.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.NotFoundException;

import java.util.UUID;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException(UUID productId) {
        super(ErrorMessages.PRODUCT_NOT_FOUND.message(productId));
    }

}
