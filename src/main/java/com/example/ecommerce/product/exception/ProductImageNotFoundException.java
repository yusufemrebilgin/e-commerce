package com.example.ecommerce.product.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.NotFoundException;

public class ProductImageNotFoundException extends NotFoundException {

    public ProductImageNotFoundException(String filename) {
        super(ErrorMessages.PRODUCT_IMAGE_NOT_FOUND.message(filename));
    }

}
