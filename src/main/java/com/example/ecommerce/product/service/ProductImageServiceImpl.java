package com.example.ecommerce.product.service;

import com.example.ecommerce.product.exception.ProductImageNotFoundException;
import com.example.ecommerce.product.exception.EmptyFileException;
import com.example.ecommerce.product.exception.FileStorageException;
import com.example.ecommerce.product.exception.InvalidFileTypeException;
import com.example.ecommerce.product.model.Product;
import com.example.ecommerce.product.model.ProductImage;
import com.example.ecommerce.product.payload.response.ProductImageResponse;
import com.example.ecommerce.product.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductServiceImpl productService;
    private final ProductImageRepository productImageRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductImageServiceImpl.class);

    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/png", "image/jpeg");

    @Override
    public List<String> uploadProductImages(String productId, MultipartFile[] files, String urlTemplate) {
        Product existingProduct = productService.findProductEntityById(productId);
        logger.info("Starting upload process for product {} with {} files", productId, files.length);
        List<String> uploadedImageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                logger.info("Validating file {}", file.getOriginalFilename());
                validateFile(file);

                String uniqueFilename = "";
                if (file.getOriginalFilename() != null) {
                    uniqueFilename = generateUniqueFilename(file.getOriginalFilename());
                }

                String url = urlTemplate.replace("{filename}", uniqueFilename);

                ProductImage image = ProductImage.builder()
                        .url(url)
                        .product(existingProduct)
                        .type(file.getContentType())
                        .filename(uniqueFilename)
                        .imageData(file.getBytes())
                        .build();

                productImageRepository.save(image);
                uploadedImageUrls.add(url);
                logger.info("File {} uploaded successfully with new name: {}", file.getOriginalFilename(), uniqueFilename);
            } catch (IOException ex) {
                logger.error("Error while processing the file {}: {}", file.getOriginalFilename(), ex.getMessage());
                throw new FileStorageException(file.getOriginalFilename(), ex);
            }
        }

        return uploadedImageUrls;
    }

    @Override
    public ProductImageResponse getProductImage(String productId, String filename) {
        ProductImage image = findProductImage(productId, filename);
        return new ProductImageResponse(image.getType(), image.getImageData());
    }

    @Override
    public List<String> getAllProductImageUrls(String productId) {
        return productImageRepository.findAllByProductId(productId).stream()
                .map(ProductImage::getUrl)
                .toList();
    }

    @Override
    public void deleteProductImages(String productId, Set<String> filenames) {
        int imageCountForProduct = productImageRepository.countProductImageByProductId(productId);
        if (imageCountForProduct <= 0) {
            throw new ProductImageNotFoundException();
        }
        filenames.forEach(filename -> deleteProductImage(productId, filename));
    }

    @Override
    public void deleteAllProductImages(String productId) {
        List<ProductImage> images = productImageRepository.findAllByProductId(productId);
        productImageRepository.deleteAll(images);
        logger.info("All images deleted for product {}", productId);
    }

    /**
     * Validates an uploaded file by checking if it's empty and if its MIME type is allowed.
     *
     * @param file a {@link MultipartFile} to validate
     * @throws EmptyFileException       if the file is empty
     * @throws InvalidFileTypeException if the file type is not allowed
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            logger.warn("Uploaded file is empty: {}", file.getOriginalFilename());
            throw new EmptyFileException(file.getOriginalFilename());
        }

        if (!ALLOWED_MIME_TYPES.contains(file.getContentType())) {
            logger.warn("Invalid file type for file '{}'. Allowed types are: {}", file.getOriginalFilename(), ALLOWED_MIME_TYPES);
            throw new InvalidFileTypeException(file.getOriginalFilename(), ALLOWED_MIME_TYPES.toString());
        }

        logger.info("File {} validated successfully", file.getOriginalFilename());
    }

    /**
     * Generates a unique filename based on a UUID to avoid conflicts.
     *
     * @param originalFilename original name of the file
     * @return unique filename
     */
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

    private ProductImage findProductImage(String productId, String filename) {
        return productImageRepository.findByProductIdAndFilename(productId, filename)
                .orElseThrow(() -> new ProductImageNotFoundException(filename));
    }

    private void deleteProductImage(String productId, String filename) {
        productImageRepository.delete(findProductImage(productId, filename));
        logger.info("Image deleted {}", filename);
    }

}
