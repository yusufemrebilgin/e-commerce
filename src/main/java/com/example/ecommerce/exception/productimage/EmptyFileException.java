package com.example.ecommerce.exception.productimage;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.BadRequestException;

public class EmptyFileException extends BadRequestException {

    public EmptyFileException(String filename) {
        super(ErrorMessages.EMPTY_FILE.message(filename));
    }

}
