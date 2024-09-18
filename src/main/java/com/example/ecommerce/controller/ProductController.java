package com.example.ecommerce.controller;

import com.example.ecommerce.payload.dto.ProductDto;
import com.example.ecommerce.payload.dto.ProductImageDto;
import com.example.ecommerce.payload.request.product.CreateProductRequest;
import com.example.ecommerce.payload.request.product.UpdateProductRequest;
import com.example.ecommerce.payload.response.PaginatedResponse;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @GetMapping("/products")
    public ResponseEntity<PaginatedResponse<ProductDto>> getAllProducts(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/products/search")
    public ResponseEntity<PaginatedResponse<ProductDto>> getAllProductsByName(
            @RequestParam String name, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProductsByName(name, pageable));
    }

    @GetMapping("/categories/{categoryId}/products")
    public ResponseEntity<PaginatedResponse<ProductDto>> getAllProductsByCategory(
            @PathVariable Long categoryId, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProductsByCategory(categoryId, pageable));
    }

    @PostMapping("/categories/{categoryId}/products")
    public ResponseEntity<ProductDto> createProduct(@PathVariable Long categoryId,
                                                    @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(categoryId, request));
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable UUID productId,
                                                    @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/products/{productId}/images")
    public ResponseEntity<List<ProductImageDto>> uploadImage(@PathVariable UUID productId,
                                                             @RequestParam("image") MultipartFile[] files) throws IOException {
        return ResponseEntity.ok(productService.uploadProductImages(productId, files));
    }

    @DeleteMapping("/products/{productId}/images")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID productId,
                                            @RequestBody List<String> filenames) {
        productService.deleteProductImages(productId, filenames);
        return ResponseEntity.noContent().build();
    }

}
