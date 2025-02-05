package com.example.ecommerce.product.controller;

import com.example.ecommerce.product.payload.request.CreateProductRequest;
import com.example.ecommerce.product.payload.request.UpdateProductRequest;
import com.example.ecommerce.shared.payload.PaginatedResponse;
import com.example.ecommerce.product.payload.response.ProductResponse;
import com.example.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param productId then unique identifier of the product to be retrieved
     * @return a {@link ResponseEntity} containing the {@link ProductResponse}
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    /**
     * Retrieves all products with pagination.
     *
     * @param pageable pagination information
     * @return a {@link ResponseEntity} containing a {@link PaginatedResponse} of {@link ProductResponse}
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<ProductResponse>> getAllProducts(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    /**
     * Retrieves products by their name with pagination.
     *
     * @param name the name of the products to search for
     * @param pageable pagination information
     * @return a {@link ResponseEntity} containing a {@link PaginatedResponse} of {@link ProductResponse}
     */
    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<ProductResponse>> getAllProductsByName(
            @RequestParam String name, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProductsByName(name, pageable));
    }

    /**
     * Retrieves products by category identifier with pagination.
     *
     * @param categoryId the unique identifier of the category
     * @param pageable pagination information
     * @return a {@link ResponseEntity} containing a {@link PaginatedResponse} of {@link ProductResponse}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PaginatedResponse<ProductResponse>> getAllProductsByCategoryId(
            @PathVariable Long categoryId, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProductsByCategoryId(categoryId, pageable));
    }

    /**
     * Creates a new product.
     *
     * @param request the {@link CreateProductRequest} containing the details of the product to be created
     * @return a {@link ResponseEntity} containing the created {@link ProductResponse}
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    /**
     * Updates an existing product.
     *
     * @param productId the unique identifier of the product to be updated
     * @param request the {@link UpdateProductRequest} containing the updated details of the product
     * @return a {@link ResponseEntity} containing the updated {@link ProductResponse}
     */
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID productId, @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    /**
     * Deletes a product by its unique identifier.
     *
     * @param productId the unique identifier of the product to be deleted
     * @return a {@link ResponseEntity} indicating the deletion was successful
     */
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

}
