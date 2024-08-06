package com.example.ecommerce.exception;

public class ProductImageNotFoundException extends NotFoundException {

    public ProductImageNotFoundException(String filename) {
        super(String.format(DEFAULT_FORMAT_WITH_NAME, "Image", filename));
    }

}
