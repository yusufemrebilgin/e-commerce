package com.example.ecommerce.product.exception;

import com.example.ecommerce.shared.exception.NotFoundException;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException(String productId) {
        super("Product not found with id '" + productId + "'");
    }

}
