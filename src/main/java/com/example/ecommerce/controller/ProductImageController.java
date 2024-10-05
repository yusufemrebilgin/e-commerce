package com.example.ecommerce.controller;

import com.example.ecommerce.payload.response.ProductImageResponse;
import com.example.ecommerce.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/{productId}/images")
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping
    public ResponseEntity<Void> uploadImages(@PathVariable UUID productId, @RequestParam("image") MultipartFile[] files) {
        String urlTemplate = ServletUriComponentsBuilder.fromCurrentRequest().path("/{filename}").toUriString();
        productImageService.uploadProductImages(productId, files, urlTemplate);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable UUID productId, @PathVariable String filename) {
        ProductImageResponse response = productImageService.getProductImage(productId, filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.contentType()))
                .body(response.imageData());
    }

    @GetMapping
    public ResponseEntity<?> getAllImages(@PathVariable UUID productId) {
        return ResponseEntity.ok(productImageService.getAllProductImages(productId));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteImages(@PathVariable UUID productId, @RequestBody Set<String> filenames) {
        productImageService.deleteProductImages(productId, filenames);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<Void> deleteAllImages(@PathVariable UUID productId) {
        productImageService.deleteAllProductImages(productId);
        return ResponseEntity.ok().build();
    }

}
