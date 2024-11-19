package com.example.ecommerce.exception.productimage;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.BadRequestException;

public class InvalidFileTypeException extends BadRequestException {

    public InvalidFileTypeException(String filename, String allowedTypes) {
        super(ErrorMessages.INVALID_FILE_TYPE.message(filename, allowedTypes));
    }

}
