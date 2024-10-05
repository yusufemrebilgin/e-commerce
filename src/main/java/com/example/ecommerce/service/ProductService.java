package com.example.ecommerce.service;

import com.example.ecommerce.exception.cart.InsufficientStockException;
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

    protected Product findProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found with id {}", productId);
                    return new ProductNotFoundException(productId);
                });
    }

    public ProductResponse getProductById(UUID productId) {
        return productMapper.mapToResponse(findProductById(productId));
    }

    public PaginatedResponse<ProductResponse> getAllProducts(Pageable pageable) {
        return paginationMapper.toPaginatedResponse(productRepository.findAll(pageable), productMapper);
    }

    public PaginatedResponse<ProductResponse> getAllProductsByName(String name, Pageable pageable) {
        return paginationMapper.toPaginatedResponse(
                productRepository.findAllByNameIgnoreCaseStartingWith(name, pageable),
                productMapper
        );
    }

    public PaginatedResponse<ProductResponse> getAllProductsByCategoryId(Long categoryId, Pageable pageable) {
        return paginationMapper.toPaginatedResponse(
                productRepository.findAllByCategoryId(categoryId, pageable),
                productMapper
        );
    }

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

    public ProductResponse updateProduct(UUID productId, UpdateProductRequest request) {
        Product existingProduct = findProductById(productId);
        existingProduct.setName(request.name());
        existingProduct.setDescription(request.description());
        existingProduct.setStock(request.stock());
        existingProduct.setPrice(request.price());

        Discount discount = existingProduct.getDiscount();
        if (request.discountPercentage() != null)
            discount.setPercentage(request.discountPercentage());
        if (request.discountStart() != null)
            discount.setStart(request.discountStart());
        if (request.discountEnd() != null)
            discount.setEnd(request.discountEnd());

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product '{}' updated", updatedProduct.getName());
        return productMapper.mapToResponse(updatedProduct);
    }

    public void deleteProduct(UUID productId) {
        Product productToBeDeleted = findProductById(productId);
        log.info("Product '{}' deleted", productToBeDeleted.getName());
        productRepository.delete(productToBeDeleted);
    }

    protected void checkStock(UUID productId, int requestedQuantity) {
        int availableStock = productRepository.findStockQuantityByProductId(productId);
        if (availableStock < requestedQuantity) {
            throw new InsufficientStockException(availableStock, requestedQuantity);
        }
    }

    @Transactional
    protected void decreaseStock(UUID productId, int quantity) {
        Product existingProduct = findProductById(productId);
        if (quantity < 0 || !existingProduct.hasSufficientStock(quantity)) {
            throw new InsufficientStockException("Not enough stock or invalid quantity");
        }
        existingProduct.setStock(existingProduct.getStock() - quantity);
        productRepository.save(existingProduct);
    }

}
