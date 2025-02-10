package com.example.ecommerce.product.service;

import com.example.ecommerce.product.exception.InsufficientStockException;
import com.example.ecommerce.product.exception.ProductNotFoundException;
import com.example.ecommerce.product.model.Product;
import com.example.ecommerce.product.payload.request.CreateProductRequest;
import com.example.ecommerce.product.payload.request.UpdateProductRequest;
import com.example.ecommerce.product.payload.response.ProductResponse;
import com.example.ecommerce.shared.payload.PaginatedResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing products in the e-commerce system.
 * Provides methods for creating, updating, retrieving, and deleting products,
 * as well as managing stock quantities.
 */
public interface ProductService {

    /**
     * Finds a product entity by its unique identifier.
     *
     * @param productId the unique identifier of the product
     * @return the product entity
     * @throws ProductNotFoundException if the product is not found
     */
    Product findProductEntityById(String productId);

    /**
     * Retrieves a product's details by its unique identifier.
     *
     * @param productId the unique identifier of the product
     * @return the product details
     * @throws ProductNotFoundException if the product is not found
     */
    ProductResponse getProductById(String productId);

    /**
     * Retrieves all products in a paginated response.
     *
     * @param pageable pagination details
     * @return a paginated response containing product details
     */
    PaginatedResponse<ProductResponse> getAllProducts(Pageable pageable);

    /**
     * Retrieves products by name, supporting pagination.
     *
     * @param name the name or partial name of the product
     * @param pageable pagination details
     * @return a paginated response containing product details matching the name
     */
    PaginatedResponse<ProductResponse> getAllProductsByName(String name, Pageable pageable);

    /**
     * Retrieves products by category name, supporting pagination.
     *
     * @param categoryName the category name
     * @param pageable pagination details
     * @return a paginated response containing products in the specified category
     */
    PaginatedResponse<ProductResponse> getAllProductsByCategoryName(String categoryName, Pageable pageable);

    /**
     * Creates a new product based on the provided request data
     *
     * @param createRequest the request containing product details
     * @return the created product details
     */
    ProductResponse createProduct(CreateProductRequest createRequest);

    /**
     * Updates an existing product with the provided request data
     *
     * @param productId the unique identifier of the product to update
     * @param updateRequest the request containing updated product details
     * @return the updated product details
     * @throws ProductNotFoundException if the product is not found
     */
    ProductResponse updateProduct(String productId, UpdateProductRequest updateRequest);

    /**
     * Deletes a product by its unique identifier.
     *
     * @param productId the unique identifier of the product to delete
     * @throws ProductNotFoundException if the product is not found
     */
    void deleteProduct(String productId);

    /**
     * Checks if sufficient stock is available for a product.
     *
     * @param productId the unique identifier of the product
     * @param requestedQuantity the quantity to check against available stock
     * @throws InsufficientStockException if there are not enough stocks
     */
    void checkStock(String productId, int requestedQuantity);

    /**
     * Increases the stock quantity for a product.
     *
     * @param productId the unique identifier of the product
     * @param quantity the quantity to add to the stock
     * @throws IllegalArgumentException if the quantity is negative
     */
    void increaseStock(String productId, int quantity);

    /**
     * Decreases the stock quantity for a product.
     *
     * @param productId the unique identifier of the product
     * @param quantity the quantity to subtract from the stock
     * @throws IllegalArgumentException if the quantity is negative
     * @throws InsufficientStockException if there are not enough stocks to decrease
     */
    void decreaseStock(String productId, int quantity);

}
