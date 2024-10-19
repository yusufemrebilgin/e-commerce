package com.example.ecommerce.exception.productimage;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.InternalServerException;

public class FileStorageException extends InternalServerException {

    public FileStorageException(String filename, Throwable cause) {
        super(ErrorMessages.FILE_STORAGE_FAILED.message(filename), cause);
    }

}
