package com.example.ecommerce.product.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.BadRequestException;

public class EmptyFileException extends BadRequestException {

    public EmptyFileException(String filename) {
        super(ErrorMessages.EMPTY_FILE.message(filename));
    }

}
