package com.example.ecommerce.product.controller;

import com.example.ecommerce.product.payload.response.ProductImageResponse;
import com.example.ecommerce.product.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/{productId}/images")
public class ProductImageController {

    private final ProductImageService productImageService;

    /**
     * Uploads images for a specific product and returns the URLs of the uploaded images.
     *
     * @param productId the unique identifier of the product for which images are being uploaded
     * @param files     the array of {@link MultipartFile} image files to be uploaded
     * @return a {@link ResponseEntity} containing a {@link List} of URLs for the uploaded images
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> uploadImages(@PathVariable UUID productId, @RequestParam("image") MultipartFile[] files) {
        // Properly encode the URL to handle special characters
        String urlTemplate = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{filename}")
                .encode()
                .toUriString();

        return ResponseEntity.ok(productImageService.uploadProductImages(productId, files, urlTemplate));
    }

    /**
     * Retrieves an image by its filename for a specific product.
     *
     * @param productId the unique identifier oof the product
     * @param filename  the name of the image file to be retrieved
     * @return a {@link ResponseEntity} containing image data
     */
    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable UUID productId, @PathVariable String filename) {
        ProductImageResponse response = productImageService.getProductImage(productId, filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.contentType()))
                .body(response.imageData());
    }

    /**
     * Retrieves all image URLs associated with a specific product.
     *
     * @param productId the unique identifier of the product
     * @return a {@link ResponseEntity} containing list of product image URLs
     */
    @GetMapping
    public ResponseEntity<List<String>> getAllImages(@PathVariable UUID productId) {
        return ResponseEntity.ok(productImageService.getAllProductImageUrls(productId));
    }

    /**
     * Deletes specified images for a specific product.
     *
     * @param productId the unique identifier of the product
     * @param filenames the set of image filenames to be deleted
     * @return a {@link ResponseEntity} indicating the deletion was successful
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteImages(@PathVariable UUID productId, @RequestBody Set<String> filenames) {
        productImageService.deleteProductImages(productId, filenames);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes all images associated with a specific product.
     *
     * @param productId the unique identifier of the product
     * @return a {@link ResponseEntity} indicating the deletion was successful
     */
    @DeleteMapping("/delete-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAllImages(@PathVariable UUID productId) {
        productImageService.deleteAllProductImages(productId);
        return ResponseEntity.ok().build();
    }

}
