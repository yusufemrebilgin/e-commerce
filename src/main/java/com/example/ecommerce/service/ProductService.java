package com.example.ecommerce.service;

import com.example.ecommerce.exception.product.InsufficientStockException;
import com.example.ecommerce.exception.product.ProductNotFoundException;
import com.example.ecommerce.mapper.PaginationMapper;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.embeddable.Discount;
import com.example.ecommerce.payload.request.product.CreateProductRequest;
import com.example.ecommerce.payload.request.product.UpdateProductRequest;
import com.example.ecommerce.payload.response.PaginatedResponse;
import com.example.ecommerce.payload.response.ProductResponse;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final CategoryService categoryService;

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    private final PaginationMapper paginationMapper;

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    /**
     * Retrieves a product by its ID.
     *
     * @param productId UUID of the product to retrieve
     * @return found {@link Product}
     * @throws ProductNotFoundException if product image is not found
     */
    protected Product findProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found with id {}", productId);
                    return new ProductNotFoundException(productId);
                });
    }

    /**
     * Retrieves product details as a {@code ProductResponse} object.
     *
     * @param productId UUID of the product to retrieve
     * @return {@link ProductResponse} containing product details
     */
    public ProductResponse getProductById(UUID productId) {
        return productMapper.mapToResponse(findProductById(productId));
    }

    /**
     * Retrieves all products with pagination support.
     *
     * @param pageable pagination information
     * @return a paginated list of {@link ProductResponse}
     */
    public PaginatedResponse<ProductResponse> getAllProducts(Pageable pageable) {
        return paginationMapper.toPaginatedResponse(productRepository.findAll(pageable), productMapper);
    }

    /**
     * Retrieves products by their name with pagination support.
     *
     * @param name     product name to filter products by
     * @param pageable pagination information
     * @return a paginated list of {@link ProductResponse}
     */
    public PaginatedResponse<ProductResponse> getAllProductsByName(String name, Pageable pageable) {
        return paginationMapper.toPaginatedResponse(
                productRepository.findAllByNameIgnoreCaseStartingWith(name, pageable),
                productMapper
        );
    }

    /**
     * Retrieves products by their category ID with pagination support.
     *
     * @param categoryId ID of the category to filter products by
     * @param pageable   pagination information
     * @return a paginated list of {@link ProductResponse}
     */
    public PaginatedResponse<ProductResponse> getAllProductsByCategoryId(Long categoryId, Pageable pageable) {
        return paginationMapper.toPaginatedResponse(
                productRepository.findAllByCategoryId(categoryId, pageable),
                productMapper
        );
    }

    /**
     * Creates a new product based on provided request.
     *
     * @param request the {@link CreateProductRequest} containing product details
     * @return newly created {@link ProductResponse}
     */
    public ProductResponse createProduct(CreateProductRequest request) {
        Discount discount = new Discount(
                request.discountPercentage(),
                request.discountStart(),
                request.discountEnd()
        );

        Product product = Product.builder()
                .name(request.name())
                .category(categoryService.getCategoryById(request.categoryId()))
                .description(request.description())
                .stock(request.stock())
                .price(request.price())
                .discount(discount)
                .images(List.of())
                .build();

        log.info("Product '{}' created", product.getName());
        return productMapper.mapToResponse(productRepository.save(product));
    }

    /**
     * Updates an existing product based on the provided request.
     *
     * @param productId UUID of the product to update
     * @param request   the {@link UpdateProductRequest} containing updated product details
     * @return updated {@link ProductResponse}
     */
    public ProductResponse updateProduct(UUID productId, UpdateProductRequest request) {
        Product existingProduct = findProductById(productId);
        productMapper.updateProductFromRequest(request, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product '{}' updated", updatedProduct.getName());
        return productMapper.mapToResponse(updatedProduct);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param productId UUID of the product to delete
     */
    public void deleteProduct(UUID productId) {
        Product productToBeDeleted = findProductById(productId);
        log.info("Product '{}' deleted", productToBeDeleted.getName());
        productRepository.delete(productToBeDeleted);
    }

    /**
     * Checks if there is sufficient stock for a product
     *
     * @param productId         UUID of the product to check
     * @param requestedQuantity requested quantity
     * @throws InsufficientStockException if available stock is less than requested quantity
     */
    protected void checkStock(UUID productId, int requestedQuantity) {
        int availableStock = productRepository.findStockQuantityByProductId(productId);
        if (availableStock < requestedQuantity) {
            throw new InsufficientStockException(availableStock, requestedQuantity);
        }
    }

    /**
     * Increase stock quantity for a product.
     *
     * @param productId UUID of the product
     * @param quantity  quantity value to increase
     * @throws IllegalArgumentException if quantity is negative
     */
    @Transactional
    protected void increaseStock(UUID productId, int quantity) {
        log.info("Attempting to increase stock for product {} by quantity: {}", productId, quantity);
        if (quantity < 0) {
            log.error("Requested quantity must be a positive number for product {}", productId);
            throw new IllegalArgumentException("Quantity must be positive number");
        }
        Product existingProduct = findProductById(productId);
        int newStock = existingProduct.getStock() + quantity;
        existingProduct.setStock(newStock);

        log.info("Successfully increased stock for product {}. New stock: {}", productId, newStock);
        productRepository.save(existingProduct);
    }

    /**
     * Decreases stock quantity for a product.
     *
     * @param productId UUID of the product
     * @param quantity  quantity value to decrease
     * @throws IllegalArgumentException   if quantity is negative
     * @throws InsufficientStockException if quantity is negative or there is not enough stock
     */
    @Transactional
    protected void decreaseStock(UUID productId, int quantity) {
        log.info("Attempting to decrease stock for product {} by quantity: {}", productId, quantity);
        if (quantity < 0) {
            log.error("Negative quantity provided for product {}. Requested: {}", productId, quantity);
            throw new IllegalArgumentException("Quantity must be positive number");
        }

        Product existingProduct = findProductById(productId);
        if (!existingProduct.hasSufficientStock(quantity)) {
            log.error(
                    "Insufficient stock for product {}. Requested: {}, Available: {}",
                    productId, quantity, existingProduct.getStock()
            );
            throw new InsufficientStockException(existingProduct.getStock(), quantity);
        }

        int newStock = existingProduct.getStock() - quantity;
        existingProduct.setStock(newStock);

        log.info("Successfully decreased stock for product {}. New stock: {}", productId, newStock);
        productRepository.save(existingProduct);
    }

}
