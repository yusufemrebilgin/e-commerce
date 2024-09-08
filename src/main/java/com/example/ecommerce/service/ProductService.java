package com.example.ecommerce.service;

import com.example.ecommerce.exception.product.ProductImageNotFoundException;
import com.example.ecommerce.exception.product.ProductNotFoundException;
import com.example.ecommerce.mapper.PaginationMapper;
import com.example.ecommerce.mapper.ProductImageMapper;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ProductImage;
import com.example.ecommerce.payload.dto.ProductDto;
import com.example.ecommerce.payload.dto.ProductImageDto;
import com.example.ecommerce.payload.request.product.CreateProductRequest;
import com.example.ecommerce.payload.request.product.UpdateProductRequest;
import com.example.ecommerce.payload.response.PaginatedResponse;
import com.example.ecommerce.repository.ProductImageRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final PaginationMapper paginationMapper;

    public ProductDto getProduct(UUID productId) {
        return productMapper.mapToDto(getProductById(productId));
    }

    protected Product getProductById(UUID productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
    }

    protected Integer getAvailableStockQuantity(UUID productId) {
        return productRepository.findStockQuantityByProductId(productId);
    }

    public PaginatedResponse<ProductDto> getAllProducts(Pageable pageable) {
        return paginationMapper.toPaginatedResponse(productRepository.findAll(pageable), productMapper);
    }

    public PaginatedResponse<ProductDto> getAllProductsByCategory(Long categoryId, Pageable pageable) {
        return paginationMapper.toPaginatedResponse(productRepository
                .findAllByCategoryId(categoryId, pageable), productMapper);
    }

    public PaginatedResponse<ProductDto> getAllProductsByName(String name, Pageable pageable) {
        return paginationMapper.toPaginatedResponse(productRepository
                .findAllByNameIgnoreCaseStartingWith(name, pageable), productMapper);
    }

    public ProductDto createProduct(Long categoryId, CreateProductRequest request) {
        Product product = productMapper.mapToEntity(request);
        Category category = categoryService.getCategoryById(categoryId);

        product.setCategory(category);
        updateDiscountIfApplicable(product);

        return productMapper.mapToDto(productRepository.save(product));
    }

    public ProductDto updateProduct(UUID productId, UpdateProductRequest request) {
        Product product = getProductById(productId);
        log.info("Existing product {}", product);

        productMapper.updateProductFromDto(request, product);
        updateDiscountIfApplicable(product);

        Product updatedProduct = productRepository.save(product);
        log.info("Updated product {}", updatedProduct);

        return productMapper.mapToDto(updatedProduct);
    }

    public void deleteProduct(UUID productId) {
        Product product = getProductById(productId);
        log.info("Deleted product with id {}", product.getId());
        productRepository.delete(product);
    }

    @Transactional
    public List<ProductImageDto> uploadProductImages(UUID productId, MultipartFile[] files) throws IOException {

        Product product = getProductById(productId);
        List<ProductImage> images = Objects.requireNonNullElse(product.getImages(), new ArrayList<>());

        for (MultipartFile file : files) {
            String uniqueFilename = "";
            if (file.getOriginalFilename() != null) {
                uniqueFilename = generateUniqueFileName(file.getOriginalFilename());
            }

            String imageUrl = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{filename}")
                    .buildAndExpand(uniqueFilename)
                    .toUriString();

            ProductImage image = ProductImage.builder()
                    .url(imageUrl)
                    .filename(uniqueFilename)
                    .image(ImageUtils.compress(file, 800, 600))
                    .product(product)
                    .build();

            images.add(productImageRepository.save(image));
        }

        return productImageMapper.mapToDtoList(images, productImageMapper);
    }

    @Transactional
    public void deleteProductImages(UUID productId, List<String> filenames) {
        for (String filename : filenames) {
            ProductImage productImage = productImageRepository.findByFilename(filename)
                    .orElseThrow(() -> new ProductImageNotFoundException(filename));
            productImageRepository.delete(productImage);
            log.info("Image deleted {}", productImage.getFilename());
        }
    }

    @Transactional
    void decreaseStockForOrder(UUID productId, int quantity) {
        Product product = getProductById(productId);
        if (quantity < 0 || product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Invalid quantity parameter");
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
    }

    private void updateDiscountIfApplicable(Product product) {
        if (!areDiscountFieldsValid(product)) {
            product.setDiscountAvailable(false);
            resetDiscountFields(product);
            return;
        }

        if (isDiscountExpired(product.getDiscountEnd())) {
            log.info("Discount is expired for product {}", product.getId());
            product.setDiscountAvailable(false);
            resetDiscountFields(product);
        } else {
            product.setDiscountAvailable(true);
            product.setDiscountedPrice(calculateDiscountPrice(
                    product.getPrice(),
                    product.getDiscountPercentage()
            ));
        }
    }

    private boolean areDiscountFieldsValid(Product p) {
        // All discount fields must be either all defined or all null
        boolean allDefined = p.getDiscountPercentage() != null
                && p.getDiscountStart() != null
                && p.getDiscountEnd() != null;

        boolean allNull = p.getDiscountPercentage() == null
                && p.getDiscountStart() == null
                && p.getDiscountEnd() == null;

        return allDefined || allNull;
    }

    private BigDecimal calculateDiscountPrice(BigDecimal originalPrice, Double discountPercentage) {
        if (originalPrice == null || discountPercentage == null) {
            return null; // determines that discount is not defined
        }

        BigDecimal discount = BigDecimal.valueOf(discountPercentage / 100);
        BigDecimal discountAmount = originalPrice.multiply(discount);
        return originalPrice.subtract(discountAmount);
    }

    private boolean isDiscountExpired(LocalDateTime end) {
        return end != null && LocalDateTime.now().isAfter(end);
    }

    private void resetDiscountFields(Product product) {
        product.setDiscountPercentage(null);
        product.setDiscountStart(null);
        product.setDiscountEnd(null);
        product.setDiscountedPrice(null);
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = "", uniqueFilename;

        int i = originalFilename.lastIndexOf(".");
        if (i >= 0)
            extension = originalFilename.substring(i);

        do {
            uniqueFilename = UUID.randomUUID().toString().substring(0, 10) + extension;
        } while (productImageRepository.existsByFilename(uniqueFilename));

        return uniqueFilename;
    }

}
