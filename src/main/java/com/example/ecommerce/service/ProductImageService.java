package com.example.ecommerce.service;

import com.example.ecommerce.exception.product.ProductImageNotFoundException;
import com.example.ecommerce.exception.productimage.EmptyFileException;
import com.example.ecommerce.exception.productimage.FileStorageException;
import com.example.ecommerce.exception.productimage.InvalidFileTypeException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ProductImage;
import com.example.ecommerce.payload.response.ProductImageResponse;
import com.example.ecommerce.repository.ProductImageRepository;
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
public class ProductImageService {

    private final ProductService productService;
    private final ProductImageRepository productImageRepository;

    private static final Logger log = LoggerFactory.getLogger(ProductImageService.class);

    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/png", "image/jpeg");

    /**
     * Uploads multiple product images, validates the file types, and saves them to database.
     *
     * @param productId   UUID of the product to which images belong
     * @param files       an array of {@link MultipartFile} containing images to upload
     * @param urlTemplate URL template for the image location
     * @return a {@link List} of uploaded {@link ProductImage} URLs
     * @throws FileStorageException     if there is an I/O error while processing the file
     * @throws EmptyFileException       if the uploaded file is empty
     * @throws InvalidFileTypeException if the file type is not allowed
     */
    public List<String> uploadProductImages(UUID productId, MultipartFile[] files, String urlTemplate) {
        Product existingProduct = productService.findProductById(productId);
        log.info("Starting upload process for product {} with {} files", productId, files.length);
        List<String> uploadedImageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                log.info("Validating file {}", file.getOriginalFilename());
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
                log.info("File {} uploaded successfully with new name: {}", file.getOriginalFilename(), uniqueFilename);
            } catch (IOException ex) {
                log.error("Error while processing the file {}: {}", file.getOriginalFilename(), ex.getMessage());
                throw new FileStorageException(file.getOriginalFilename(), ex);
            }
        }

        return uploadedImageUrls;
    }

    /**
     * Retrieves a product image by its filename and product ID.
     *
     * @param productId UUID of the product
     * @param filename  name of the image file
     * @return a {@link ProductImageResponse} containing image data and content type
     * @throws ProductImageNotFoundException if image is not found
     */
    public ProductImageResponse getProductImage(UUID productId, String filename) {
        ProductImage image = findProductImage(productId, filename);
        return new ProductImageResponse(image.getType(), image.getImageData());
    }

    /**
     * Retrieves a list of all product image URLs for a given product.
     *
     * @param productId UUID of the product
     * @return a list of {@link ProductImage} URLs
     */
    public List<String> getAllProductImageUrls(UUID productId) {
        return productImageRepository.findAllByProductId(productId).stream()
                .map(ProductImage::getUrl)
                .toList();
    }

    /**
     * Deletes all specified product images for a given product.
     *
     * @param productId UUID of the product
     * @param filenames a set of filenames to delete
     * @throws ProductImageNotFoundException if no images are found for product
     */
    public void deleteProductImages(UUID productId, Set<String> filenames) {
        int imageCountForProduct = productImageRepository.countProductImageByProductId(productId);
        if (imageCountForProduct <= 0) {
            throw new ProductImageNotFoundException("There are no images to delete");
        }
        filenames.forEach(filename -> deleteProductImage(productId, filename));
    }

    /**
     * Deletes all images associated with a product.
     *
     * @param productId UUID of the product
     */
    public void deleteAllProductImages(UUID productId) {
        List<ProductImage> images = productImageRepository.findAllByProductId(productId);
        productImageRepository.deleteAll(images);
        log.info("All images deleted for product {}", productId);
    }

    /**
     * Validates uploaded file by checking if it's empty and if its MIME type is allowed.
     *
     * @param file a {@link MultipartFile} to validate
     * @throws EmptyFileException       if the file is empty
     * @throws InvalidFileTypeException if the file type is not allowed
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("Uploaded file is empty: {}", file.getOriginalFilename());
            throw new EmptyFileException(file.getOriginalFilename());
        }

        if (!ALLOWED_MIME_TYPES.contains(file.getContentType())) {
            log.warn("Invalid file type for file '{}'. Allowed types are: {}", file.getOriginalFilename(), ALLOWED_MIME_TYPES);
            throw new InvalidFileTypeException(file.getOriginalFilename(), ALLOWED_MIME_TYPES.toString());
        }

        log.info("File {} validated successfully", file.getOriginalFilename());
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

    /**
     * Retrieves a product image by product ID and filename.
     *
     * @param productId UUID of the product
     * @param filename  filename of the image
     * @return found {@link ProductImage}
     * @throws ProductImageNotFoundException if image is not found
     */
    private ProductImage findProductImage(UUID productId, String filename) {
        return productImageRepository.findByProductIdAndFilename(productId, filename)
                .orElseThrow(() -> new ProductImageNotFoundException(filename));
    }

    /**
     * Deletes a specified product image and logs the deletion.
     *
     * @param productId UUID of the product
     * @param filename  filename of the image to delete
     */
    private void deleteProductImage(UUID productId, String filename) {
        productImageRepository.delete(findProductImage(productId, filename));
        log.info("Image deleted {}", filename);
    }

}
