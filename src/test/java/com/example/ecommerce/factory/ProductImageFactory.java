package com.example.ecommerce.factory;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public final class ProductImageFactory {

    private ProductImageFactory() {
    }

    public static MultipartFile validImageFile() {
        return multipartFile(".jpg", "image/jpeg", "image-data".getBytes());
    }

    public static MultipartFile emptyImageFile() {
        return multipartFile(".jpg", "image/jpeg", null);
    }

    public static MultipartFile unsupportedFile() {
        return multipartFile(".pdf", "application/pdf", "file-data".getBytes());
    }

    private static MultipartFile multipartFile(String extension, String contentType, byte[] content) {
        return new MockMultipartFile(
                "test-file",
                "test-file".concat(extension),
                contentType,
                content
        );
    }

}
