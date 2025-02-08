package com.example.ecommerce.product.exception;

import com.example.ecommerce.shared.exception.BadRequestException;

public class InvalidFileTypeException extends BadRequestException {

    public InvalidFileTypeException(String filename, String allowedTypes) {
        super(String.format(
                "Invalid file type for %s. Allowed types are %s",
                filename, allowedTypes
        ));
    }

}
