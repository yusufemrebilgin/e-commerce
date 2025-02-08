package com.example.ecommerce.product.service;

import com.example.ecommerce.category.model.Category;
import com.example.ecommerce.category.service.CategoryService;
import com.example.ecommerce.product.exception.InsufficientStockException;
import com.example.ecommerce.product.exception.ProductNotFoundException;
import com.example.ecommerce.product.mapper.ProductMapper;
import com.example.ecommerce.product.model.Product;
import com.example.ecommerce.product.model.embeddable.Discount;
import com.example.ecommerce.product.payload.request.CreateProductRequest;
import com.example.ecommerce.product.payload.request.UpdateProductRequest;
import com.example.ecommerce.product.payload.response.ProductResponse;
import com.example.ecommerce.product.repository.ProductRepository;
import com.example.ecommerce.shared.payload.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    private final CategoryService categoryService;

    @Override
    public Product findProductEntityById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Product not found with id '{}'", productId);
                    return new ProductNotFoundException(productId);
                });
    }

    @Override
    public ProductResponse getProductById(String productId) {
        return productMapper.mapToResponse(findProductEntityById(productId));
    }

    @Override
    public PaginatedResponse<ProductResponse> getAllProducts(Pageable pageable) {
        return productMapper.mapToPaginatedResponse(productRepository.findAll(pageable));
    }

    @Override
    public PaginatedResponse<ProductResponse> getAllProductsByName(String name, Pageable pageable) {
        return productMapper.mapToPaginatedResponse(
                productRepository.findAllByNameIgnoreCaseStartingWith(name, pageable)
        );
    }

    @Override
    public PaginatedResponse<ProductResponse> getAllProductsByCategoryName(String categoryName, Pageable pageable) {
        return productMapper.mapToPaginatedResponse(
                productRepository.findAllByCategoryNameIgnoreCase(categoryName, pageable)
        );
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {

        Category category = categoryService.findCategoryEntityById(request.categoryId());

        Discount discount = new Discount(
                request.discountPercentage(),
                request.discountStart(),
                request.discountEnd()
        );

        Product product = Product.builder()
                .name(request.name())
                .category(category)
                .description(request.description())
                .stock(request.stock())
                .price(request.price())
                .discount(discount)
                .images(List.of())
                .build();

        product = productRepository.save(product);
        logger.info("Product '{}' created with {} stock", product.getName(), product.getStock());

        return productMapper.mapToResponse(product);
    }

    @Override
    public ProductResponse updateProduct(String productId, UpdateProductRequest request) {
        Product existingProduct = findProductEntityById(productId);
        productMapper.updateProductFromRequest(request, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        logger.info("Product '{}' updated", updatedProduct.getName());
        return productMapper.mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(String productId) {
        Product productToBeDeleted = findProductEntityById(productId);
        logger.info("Product '{}' deleted", productToBeDeleted.getName());
        productRepository.delete(productToBeDeleted);
    }

    @Override
    public void checkStock(String productId, int requestedQuantity) {
        int availableStock = productRepository.findStockQuantityByProductId(productId);
        if (availableStock < requestedQuantity) {
            throw new InsufficientStockException(availableStock, requestedQuantity);
        }
    }


    @Override
    @Transactional
    public void increaseStock(String productId, int quantity) {
        logger.info("Attempting to increase stock for product {} by quantity: {}", productId, quantity);
        if (quantity < 0) {
            logger.error("Requested quantity must be a positive number for product {}", productId);
            throw new IllegalArgumentException("Quantity must be positive number");
        }

        Product existingProduct = findProductEntityById(productId);
        int newStock = existingProduct.getStock() + quantity;
        existingProduct.setStock(newStock);

        logger.info("Successfully increased stock for product {}. New stock: {}", productId, newStock);
        productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void decreaseStock(String productId, int quantity) {
        logger.info("Attempting to decrease stock for product {} by quantity: {}", productId, quantity);
        if (quantity < 0) {
            logger.error("Negative quantity provided for product {}. Requested: {}", productId, quantity);
            throw new IllegalArgumentException("Quantity must be positive number");
        }

        Product existingProduct = findProductEntityById(productId);
        if (!existingProduct.hasSufficientStock(quantity)) {
            logger.error(
                    "Insufficient stock for product {}. Requested: {}, Available: {}",
                    productId, quantity, existingProduct.getStock()
            );
            throw new InsufficientStockException(existingProduct.getStock(), quantity);
        }

        int newStock = existingProduct.getStock() - quantity;
        existingProduct.setStock(newStock);

        logger.info("Successfully decreased stock for product {}. New stock: {}", productId, newStock);
        productRepository.save(existingProduct);
    }

}
