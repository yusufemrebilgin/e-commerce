package com.example.ecommerce.controller;

import com.example.ecommerce.payload.request.product.CreateProductRequest;
import com.example.ecommerce.payload.request.product.UpdateProductRequest;
import com.example.ecommerce.payload.response.PaginatedResponse;
import com.example.ecommerce.payload.response.ProductResponse;
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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<ProductResponse>> getAllProducts(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<ProductResponse>> getAllProductsByName(
            @RequestParam String name, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProductsByName(name, pageable));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PaginatedResponse<ProductResponse>> getAllProductsByCategoryId(
            @PathVariable Long categoryId, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProductsByCategoryId(categoryId, pageable));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID productId, @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

}
