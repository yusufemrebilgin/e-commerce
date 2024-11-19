package com.example.ecommerce.exception.productimage;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.NotFoundException;

public class ProductImageNotFoundException extends NotFoundException {

    public ProductImageNotFoundException(String filename) {
        super(ErrorMessages.PRODUCT_IMAGE_NOT_FOUND.message(filename));
    }

}
