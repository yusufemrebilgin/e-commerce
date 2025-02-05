package com.example.ecommerce.product.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.BadRequestException;

public class InvalidFileTypeException extends BadRequestException {

    public InvalidFileTypeException(String filename, String allowedTypes) {
        super(ErrorMessages.INVALID_FILE_TYPE.message(filename, allowedTypes));
    }

}
