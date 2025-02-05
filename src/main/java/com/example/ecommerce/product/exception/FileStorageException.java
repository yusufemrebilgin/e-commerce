package com.example.ecommerce.product.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.InternalServerException;

public class FileStorageException extends InternalServerException {

    public FileStorageException(String filename, Throwable cause) {
        super(ErrorMessages.FILE_STORAGE_FAILED.message(filename), cause);
    }

}
