package com.example.ecommerce.product.factory;

import com.example.ecommerce.product.model.ProductImage;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ProductImageFactory {

    private ProductImageFactory() {
    }

    public static ProductImage productImage() {
        return productImage("image.jpg");
    }

    public static ProductImage productImage(String filename) {
        return ProductImage.builder()
                .id("image-id")
                .filename(filename)
                .type("image/jpeg")
                .imageData("image-data".getBytes())
                .build();
    }

    public static List<ProductImage> list(int size, Supplier<ProductImage> productImageSupplier) {
        List<ProductImage> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(productImageSupplier.get());
        }
        return list;
    }

    public static MultipartFile validImageFile() {
        return multipartFile(".jpg", "image/jpeg", "image-data".getBytes());
    }

    public static MultipartFile validImageFile(String filename) {
        return multipartFile(filename, ".jpg", "image/jpeg", "image-data".getBytes());
    }

    public static MultipartFile emptyImageFile() {
        return multipartFile(".jpg", "image/jpeg", null);
    }

    public static MultipartFile unsupportedFile() {
        return multipartFile(".pdf", "application/pdf", "file-data".getBytes());
    }

    private static MultipartFile multipartFile(String extension, String contentType, byte[] content) {
        return multipartFile("test-file", extension, contentType, content);
    }

    private static MultipartFile multipartFile(String filename, String extension, String contentType, byte[] content) {
        return new MockMultipartFile(
                filename,
                filename.concat(extension),
                contentType,
                content
        );
    }

}
