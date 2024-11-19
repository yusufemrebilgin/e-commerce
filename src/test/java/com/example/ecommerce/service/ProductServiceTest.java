package com.example.ecommerce.service;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.product.InsufficientStockException;
import com.example.ecommerce.exception.product.ProductNotFoundException;
import com.example.ecommerce.factory.ProductFactory;
import com.example.ecommerce.mapper.PaginationMapper;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.payload.request.product.CreateProductRequest;
import com.example.ecommerce.payload.request.product.UpdateProductRequest;
import com.example.ecommerce.payload.response.PaginatedResponse;
import com.example.ecommerce.payload.response.ProductResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    ProductService productService;

    @Mock
    CategoryService categoryService;

    @Mock
    ProductMapper productMapper;

    @Mock
    ProductRepository productRepository;

    @Mock
    PaginationMapper paginationMapper;

    @ParameterizedTest
    @CsvSource({
            "0, 5, 3",
            "0, 2, 2",
            "1, 2, 1",
            "1, 3, 0"
    })
    void givenPaginationParameters_whenGetAllProducts_thenReturnPaginatedProductResponse(int page, int size, int expectedSize) {
        // given
        List<Product> products = ProductFactory.list(3, ProductFactory::product);
        Page<Product> productPage = new PageImpl<>(products.subList(page * size, Math.min((page + 1) * size, products.size())));

        List<ProductResponse> response = ProductFactory.responseList(products);
        List<ProductResponse> expectedResponseList = response.subList(0, expectedSize);

        PaginatedResponse<ProductResponse> expected = new PaginatedResponse<>(
                expectedResponseList,
                page,
                size,
                productPage.getTotalPages(),
                productPage.getNumberOfElements(),
                productPage.isLast()
        );

        given(productRepository.findAll(any(Pageable.class))).willReturn(productPage);
        given(paginationMapper.toPaginatedResponse(productPage, productMapper)).willReturn(expected);

        // when
        PaginatedResponse<ProductResponse> actual = productService.getAllProducts(PageRequest.of(page, size));

        // then
        then(actual).isNotNull();
        then(actual.page()).isEqualTo(page);
        then(actual.size()).isEqualTo(size);
        then(actual.content()).hasSize(expectedSize);
        for (int i = 0; i < expectedSize; i++) {
            then(actual.content().get(i)).isEqualTo(expected.content().get(i));
        }

        verify(productRepository, times(1)).findAll(any(Pageable.class));
        verify(paginationMapper, times(1)).toPaginatedResponse(productPage, productMapper);
    }

    @Test
    void givenValidProductId_whenProductFound_thenReturnProduct() {
        // given
        Product expected = ProductFactory.product();
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(expected));

        // when
        Product actual = productService.findProductById(expected.getId());

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        verify(productRepository, times(1)).findById(expected.getId());
    }

    @Test
    void givenInvalidProductId_whenProductNotFound_thenThrowProductNotFoundException() {
        // given
        UUID productId = UUID.randomUUID();
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        // when & then
        ProductNotFoundException ex = catchThrowableOfType(
                () -> productService.findProductById(productId),
                ProductNotFoundException.class
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining(productId.toString());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void givenValidCreateProductRequest_whenProductCreated_thenReturnProductResponse() {
        // given
        Product product = ProductFactory.product();
        CreateProductRequest request = ProductFactory.createRequest(product);
        ProductResponse expected = ProductFactory.response(product);

        given(categoryService.getCategoryById(anyLong())).willReturn(product.getCategory());
        given(productRepository.save(any(Product.class))).willReturn(product);
        given(productMapper.mapToResponse(any(Product.class))).willReturn(expected);

        // when
        ProductResponse actual = productService.createProduct(request);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).mapToResponse(any(Product.class));
    }

    @Test
    void givenValidUpdateProductRequest_whenExistingProductUpdated_thenReturnUpdatedProductResponse() {
        // given
        Product existingProduct = ProductFactory.product();
        Product updatedProduct = ProductFactory.product(existingProduct.getId(), "Updated Product");
        UpdateProductRequest request = ProductFactory.updateRequest(updatedProduct);
        ProductResponse expected = ProductFactory.response(updatedProduct);

        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(existingProduct));
        given(productRepository.save(any(Product.class))).willReturn(updatedProduct);
        given(productMapper.mapToResponse(updatedProduct)).willReturn(expected);

        // when
        ProductResponse actual = productService.updateProduct(existingProduct.getId(), request);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).mapToResponse(any(Product.class));
    }

    @Test
    void givenValidProductId_whenProductExists_thenDeleteExistingProduct() {
        // given
        Product product = ProductFactory.product();
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        // when & then
        productService.deleteProduct(product.getId());
        verify(productRepository, times(1)).delete(product);
        verify(productRepository).delete(argThat(p -> p.getId().equals(product.getId())));
    }

    @Test
    void givenInvalidProductId_whenProductNotExists_thenThrowProductNotFoundException() {
        // given
        UUID productId = UUID.randomUUID();
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        // when & then
        ProductNotFoundException ex = catchThrowableOfType(
                () -> productService.deleteProduct(productId),
                ProductNotFoundException.class
        );

        then(ex).isNotNull();
        then(ex.getMessage()).isEqualTo(ErrorMessages.PRODUCT_NOT_FOUND.message(productId));
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void givenProductIdAndQuantity_whenProductStockIsSufficient_thenNoExceptionThrown() {
        // given
        int initialStock = 1_000;
        int requestedQuantity = 50;

        Product product = ProductFactory.productWithStock(initialStock);
        given(productRepository.findStockQuantityByProductId(any(UUID.class))).willReturn(initialStock);

        // when & then
        productService.checkStock(product.getId(), requestedQuantity);
    }

    @Test
    void givenProductIdAndQuantity_whenProductStockIsInsufficient_thenThrowInsufficientStockException() {
        // given
        int initialStock = 10;
        int requestedQuantity = 500;

        Product product = ProductFactory.productWithStock(initialStock);
        given(productRepository.findStockQuantityByProductId(any(UUID.class))).willReturn(initialStock);

        // when & then
        InsufficientStockException ex = catchThrowableOfType(
                () -> productService.checkStock(product.getId(), requestedQuantity),
                InsufficientStockException.class
        );

        then(ex).isNotNull();
        then(ex.getMessage()).isEqualTo(ErrorMessages.INSUFFICIENT_STOCK.message(initialStock, requestedQuantity));
    }

    @Test
    void givenProductIdAndValidQuantity_whenIncreaseStock_thenIncreaseProductStockSuccessfully() {
        // given
        int initialStock = 100_000;
        int quantityToIncrease = 500;

        Product product = ProductFactory.productWithStock(initialStock);
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(product));

        // when
        productService.increaseStock(product.getId(), quantityToIncrease);

        // then
        int expectedStock = initialStock + quantityToIncrease;
        then(product.getStock()).isEqualTo(expectedStock);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void givenProductIdAndNegativeQuantity_whenIncreaseStock_thenThrowIllegalArgumentException() {
        // given
        int negativeQuantity = -10;
        Product product = ProductFactory.productWithStock(100);

        // when & then
        IllegalArgumentException ex = catchThrowableOfType(
                () -> productService.increaseStock(product.getId(), negativeQuantity),
                IllegalArgumentException.class
        );

        then(ex).isNotNull();
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void givenProductIdAndValidQuantity_whenDecreaseStock_thenDecreaseProductStockSuccessfully() {
        // given
        int initialStock = 100_000;
        int quantityToDecrease = 500;

        Product product = ProductFactory.productWithStock(initialStock);
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(product));

        // when
        productService.decreaseStock(product.getId(), quantityToDecrease);

        // then
        int expectedStock = initialStock - quantityToDecrease;
        then(product.getStock()).isEqualTo(expectedStock);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void givenProductIdAndInvalidQuantity_whenDecreaseStock_thenThrowInsufficientStockException() {
        // given
        int initialStock = 10;
        int quantityToDecrease = 30;

        Product product = ProductFactory.productWithStock(initialStock);
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(product));

        // when & then
        InsufficientStockException ex = catchThrowableOfType(
                () -> productService.decreaseStock(product.getId(), quantityToDecrease),
                InsufficientStockException.class
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining(ErrorMessages.INSUFFICIENT_STOCK.message(initialStock, quantityToDecrease));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void givenProductIdAndNegativeQuantity_whenDecreaseStock_thenThrowIllegalArgumentException() {
        // given
        int negativeQuantity = -10;
        Product product = ProductFactory.productWithStock(100);

        // when & then
        IllegalArgumentException ex = catchThrowableOfType(
                () -> productService.decreaseStock(product.getId(), negativeQuantity),
                IllegalArgumentException.class
        );

        then(ex).isNotNull();
        verify(productRepository, never()).save(any(Product.class));
    }

}
