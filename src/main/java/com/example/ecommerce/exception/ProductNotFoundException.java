package com.example.ecommerce.exception;

import java.util.UUID;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException() {
        super(String.format(DEFAULT_FORMAT, "Product"));
    }

    public ProductNotFoundException(UUID id) {
        super(String.format(DEFAULT_FORMAT_WITH_ID, "Product", id));
    }

}
