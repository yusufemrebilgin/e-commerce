package com.example.ecommerce.service;

import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.mapper.PaginationMapper;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.payload.dto.ProductDto;
import com.example.ecommerce.payload.request.product.CreateProductRequest;
import com.example.ecommerce.payload.request.product.UpdateProductRequest;
import com.example.ecommerce.payload.response.PaginatedResponse;
import com.example.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    ProductService productService;

    @Mock
    CategoryService categoryService;

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductMapper productMapper;

    @Mock
    PaginationMapper paginationMapper;

    @ParameterizedTest
    @CsvSource({
            "0, 5, 3",
            "0, 2, 2",
            "1, 2, 1",
            "1, 3, 0"
    })
    void givenPaginationParameters_whenGetAllProducts_returnPaginatedProductDtoResponse(int page, int size, int expectedSize) {
        // given
        List<Product> products = List.of(new Product(), new Product(), new Product());

        Page<Product> productPage = new PageImpl<>(products.subList(page * size, Math.min((page + 1) * size, products.size())));

        List<ProductDto> productDtoList = List.of(
                ProductDto.builder().build(),
                ProductDto.builder().build(),
                ProductDto.builder().build()
        );

        PaginatedResponse<ProductDto> expected = new PaginatedResponse<>(
                productDtoList.subList(0, expectedSize),
                page,
                size,
                productPage.getTotalPages(),
                productPage.getNumberOfElements(),
                productPage.isLast()
        );

        given(productRepository.findAll(any(Pageable.class))).willReturn(productPage);
        given(paginationMapper.toPaginatedResponse(productPage, productMapper)).willReturn(expected);

        // when
        PaginatedResponse<ProductDto> actual = productService.getAllProducts(page, size, "name:asc");

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(actual.content()).hasSize(expectedSize);
        then(actual.content()).containsExactlyElementsOf(expected.content());
        for (int i = 0; i < expectedSize; i++) {
            then(actual.content().get(i)).isEqualTo(expected.content().get(i));
        }
    }

    @Test
    void givenProductId_whenProductFound_returnProduct() {
        // given
        Product expected = Product.builder()
                .id(UUID.randomUUID())
                .name("Lenovo Laptop")
                .build();

        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(expected));

        // when
        Product actual = productService.getProductById(expected.getId());

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(actual.getId()).isEqualTo(expected.getId());
    }

    @Test
    void givenProductId_whenProductNotFound_throwProductNotFoundException() {
        // given
        UUID productId = UUID.randomUUID();
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when
        ProductNotFoundException ex = catchThrowableOfType(
                () -> productService.getProductById(productId),
                ProductNotFoundException.class
        );

        // then
        then(ex).isNotNull();
        then(ex).hasMessageContaining(productId.toString());
    }

    @Test
    void givenCreateProductRequest_whenCreated_returnProductDto() {
        // given
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Lenovo Laptop")
                .build();

        Category category = new Category(1L, "Laptops");

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name(request.name())
                .build();

        ProductDto expected = ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .category(category.getName())
                .build();

        given(productMapper.mapToEntity(request)).willReturn(product);
        given(categoryService.getCategoryById(anyLong())).willReturn(category);
        given(productRepository.save(any(Product.class))).willReturn(product);
        given(productMapper.mapToDto(any(Product.class))).willReturn(expected);

        // when
        ProductDto actual = productService.createProduct(category.getId(), request);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(actual.category()).isEqualTo(category.getName());
    }

    @Test
    void givenCreateProductRequestWithValidDiscount_whenCreated_returnDiscountAppliedProductDto() {
        // given
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Lenovo Laptop")
                .build();

        Category category = new Category(1L, "Laptops");

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name(request.name())
                .price(BigDecimal.valueOf(500))
                .discountPercentage(25d)
                .discountStart(LocalDateTime.now())
                .discountEnd(LocalDateTime.now().plusDays(10))
                .build();

        ProductDto expected = ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .category(category.getName())
                .hasDiscount(true)
                .pricePerUnit(BigDecimal.valueOf(500))
                .discountedPrice(BigDecimal.valueOf(375)) // 500 - (500 * 0.25)
                .discountPercentage(product.getDiscountPercentage())
                .discountStart(product.getDiscountStart())
                .discountEnd(product.getDiscountEnd())
                .build();

        given(productMapper.mapToEntity(request)).willReturn(product);
        given(categoryService.getCategoryById(anyLong())).willReturn(category);
        given(productRepository.save(product)).willReturn(product);
        given(productMapper.mapToDto(product)).willReturn(expected);

        // when
        ProductDto actual = productService.createProduct(category.getId(), request);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(product.isDiscountAvailable()).isEqualTo(true);
        then(product.getDiscountedPrice()).isEqualByComparingTo(expected.discountedPrice());
    }

    @Test
    void givenUpdateProductRequest_whenUpdated_returnUpdatedProductDto() {
        // given
        UUID productId = UUID.randomUUID();

        UpdateProductRequest request = UpdateProductRequest.builder()
                .name("RTX 3060")
                .build();

        Product existingProduct = Product.builder()
                .id(productId)
                .name("RTX 2060")
                .build();

        Product updatedProduct = Product.builder()
                .id(productId)
                .name(request.name())
                .build();

        ProductDto expected = ProductDto.builder()
                .id(productId)
                .name(updatedProduct.getName())
                .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));
        given(productRepository.save(updatedProduct)).willReturn(updatedProduct);
        given(productMapper.mapToDto(updatedProduct)).willReturn(expected);

        // when
        ProductDto actual = productService.updateProduct(productId, request);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
    }

    @Test
    void givenProductId_whenProductFound_deleteProduct() {
        // given
        UUID productId = UUID.randomUUID();

        Product product = Product.builder()
                .id(productId)
                .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        productService.deleteProduct(productId);

        // then
        verify(productRepository, times(1)).delete(product);
        verify(productRepository).delete(argThat(p -> p.getId().equals(productId)));
    }

}
