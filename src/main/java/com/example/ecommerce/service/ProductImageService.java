package com.example.ecommerce.service;

import com.example.ecommerce.exception.product.ProductImageNotFoundException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ProductImage;
import com.example.ecommerce.payload.response.ProductImageResponse;
import com.example.ecommerce.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductService productService;
    private final ProductImageRepository productImageRepository;

    private static final Logger log = LoggerFactory.getLogger(ProductImageService.class);

    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/png", "image/jpeg");

    public void uploadProductImages(UUID productId, MultipartFile[] files, String urlTemplate) {
        Product existingProduct = productService.findProductById(productId);
        for (MultipartFile file : files) {
            try {
                validateFile(file);
                String uniqueFilename = "";
                if (file.getOriginalFilename() != null) {
                    uniqueFilename = generateUniqueFilename(file.getOriginalFilename());
                }

                String url = urlTemplate.replace("{filename}", file.getOriginalFilename());

                ProductImage image = ProductImage.builder()
                        .url(url)
                        .product(existingProduct)
                        .type(file.getContentType())
                        .filename(uniqueFilename)
                        .imageData(file.getBytes())
                        .build();

                productImageRepository.save(image);
            } catch (IOException ex) {
                log.error("Error while processing the file {}: {}", file.getOriginalFilename(), ex.getMessage());
                throw new MultipartException("Failed to store file " + file.getOriginalFilename());
            }
        }
    }

    public ProductImageResponse getProductImage(UUID productId, String filename) {
        ProductImage image = findProductImage(productId, filename);
        return new ProductImageResponse(image.getType(), image.getImageData());
    }

    public List<Map<String, String>> getAllProductImages(UUID productId) {
        return productImageRepository.findAllByProductId(productId).stream()
                .map(image -> Map.of("url", image.getUrl()))
                .toList();
    }

    private ProductImage findProductImage(UUID productId, String filename) {
        return productImageRepository.findByProductIdAndFilename(productId, filename)
                .orElseThrow(() -> new ProductImageNotFoundException(filename));
    }

    private void deleteProductImage(UUID productId, String filename) {
        productImageRepository.delete(findProductImage(productId, filename));
        log.info("Image deleted {}", filename);
    }

    public void deleteProductImages(UUID productId, Set<String> filenames) {
        int imageCountForProduct = productImageRepository.countProductImageByProductId(productId);
        if (imageCountForProduct <= 0) {
            throw new ProductImageNotFoundException("There are no images to delete");
        }
        filenames.forEach(filename -> deleteProductImage(productId, filename));
    }

    public void deleteAllProductImages(UUID productId) {
        List<ProductImage> images = productImageRepository.findAllByProductId(productId);
        productImageRepository.deleteAll(images);
        log.info("All images deleted for product {}", productId);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("Uploaded file is empty: {}", file.getOriginalFilename());
            throw new MultipartException("Uploaded file is empty");
        }

        if (!ALLOWED_MIME_TYPES.contains(file.getContentType())) {
            log.warn("Invalid file type for file {}. Allowed types are: {}", file.getContentType(), ALLOWED_MIME_TYPES);
            throw new MultipartException("Invalid file type for " + file.getOriginalFilename());
        }

        log.info("File {} validated successfully", file.getOriginalFilename());
    }

    private String generateUniqueFilename(String originalFilename) {
        String extension = "", uniqueFilename;

        int i = originalFilename.lastIndexOf(".");
        if (i >= 0)
            extension = originalFilename.substring(i);

        do {
            uniqueFilename = UUID.randomUUID().toString().substring(0, 10) + extension;
        } while (productImageRepository.existsByFilename(uniqueFilename));

        return uniqueFilename;
    }

}
