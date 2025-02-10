package com.example.ecommerce.product.exception;

import com.example.ecommerce.shared.exception.NotFoundException;

public class ProductImageNotFoundException extends NotFoundException {

    public ProductImageNotFoundException() {
        super("There are no images to delete");
    }

    public ProductImageNotFoundException(String filename) {
        super("Product image not found with name " + filename);
    }

}
