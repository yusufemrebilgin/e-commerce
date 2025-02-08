package com.example.ecommerce.product.exception;

import com.example.ecommerce.shared.exception.BadRequestException;

public class EmptyFileException extends BadRequestException {

    public EmptyFileException(String filename) {
        super("File is empty " + filename);
    }

}
