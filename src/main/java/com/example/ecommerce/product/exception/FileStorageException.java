package com.example.ecommerce.product.exception;

import com.example.ecommerce.shared.exception.InternalServerException;

public class FileStorageException extends InternalServerException {

    public FileStorageException(String filename, Throwable cause) {
        super("Failed to store file " + filename, cause);
    }

}
