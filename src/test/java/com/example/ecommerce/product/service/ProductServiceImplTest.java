package com.example.ecommerce.product.service;

import com.example.ecommerce.category.service.CategoryService;
import com.example.ecommerce.product.exception.InsufficientStockException;
import com.example.ecommerce.product.exception.ProductNotFoundException;
import com.example.ecommerce.product.factory.ProductFactory;
import com.example.ecommerce.product.mapper.ProductMapper;
import com.example.ecommerce.product.model.Product;
import com.example.ecommerce.product.payload.request.CreateProductRequest;
import com.example.ecommerce.product.payload.request.UpdateProductRequest;
import com.example.ecommerce.product.payload.response.ProductResponse;
import com.example.ecommerce.product.repository.ProductRepository;
import com.example.ecommerce.shared.payload.PaginatedResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    ProductServiceImpl productService;

    @Mock
    CategoryService categoryService;

    @Mock
    ProductMapper productMapper;

    @Mock
    ProductRepository productRepository;

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
        given(productMapper.mapToPaginatedResponse(productPage)).willReturn(expected);

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
        verify(productMapper, times(1)).mapToPaginatedResponse(productPage);
    }

    @Test
    void givenValidProductId_whenProductFound_thenReturnProduct() {
        // given
        Product expected = ProductFactory.product();
        given(productRepository.findById(anyString())).willReturn(Optional.of(expected));

        // when
        Product actual = productService.findProductEntityById(expected.getId());

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        verify(productRepository, times(1)).findById(expected.getId());
    }

    @Test
    void givenInvalidProductId_whenProductNotFound_thenThrowProductNotFoundException() {
        // given
        String productId = UUID.randomUUID().toString();
        given(productRepository.findById(anyString())).willReturn(Optional.empty());

        // when & then
        ProductNotFoundException ex = catchThrowableOfType(
                ProductNotFoundException.class,
                () -> productService.findProductEntityById(productId)
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void givenValidCreateProductRequest_whenProductCreated_thenReturnProductResponse() {
        // given
        Product product = ProductFactory.product();
        CreateProductRequest request = ProductFactory.createRequest(product);
        ProductResponse expected = ProductFactory.response(product);

        given(categoryService.findCategoryEntityById(anyLong())).willReturn(product.getCategory());
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

        given(productRepository.findById(anyString())).willReturn(Optional.of(existingProduct));
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
        String productId = UUID.randomUUID().toString();
        given(productRepository.findById(anyString())).willReturn(Optional.empty());

        // when & then
        ProductNotFoundException ex = catchThrowableOfType(
                ProductNotFoundException.class,
                () -> productService.deleteProduct(productId)
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining(productId);
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void givenProductIdAndQuantity_whenProductStockIsSufficient_thenNoExceptionThrown() {
        // given
        int initialStock = 1_000;
        int requestedQuantity = 50;

        Product product = ProductFactory.productWithStock(initialStock);
        given(productRepository.findStockQuantityByProductId(anyString())).willReturn(initialStock);

        // when & then
        productService.checkStock(product.getId(), requestedQuantity);
    }

    @Test
    void givenProductIdAndQuantity_whenProductStockIsInsufficient_thenThrowInsufficientStockException() {
        // given
        int initialStock = 10;
        int requestedQuantity = 500;

        Product product = ProductFactory.productWithStock(initialStock);
        given(productRepository.findStockQuantityByProductId(anyString())).willReturn(initialStock);

        // when & then
        InsufficientStockException ex = catchThrowableOfType(
                InsufficientStockException.class,
                () -> productService.checkStock(product.getId(), requestedQuantity)
        );

        then(ex).isNotNull();
        then(ex).isExactlyInstanceOf(InsufficientStockException.class);
    }

    @Test
    void givenProductIdAndValidQuantity_whenIncreaseStock_thenIncreaseProductStockSuccessfully() {
        // given
        int initialStock = 100_000;
        int quantityToIncrease = 500;

        Product product = ProductFactory.productWithStock(initialStock);
        given(productRepository.findById(anyString())).willReturn(Optional.of(product));

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
                IllegalArgumentException.class,
                () -> productService.increaseStock(product.getId(), negativeQuantity)
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
        given(productRepository.findById(anyString())).willReturn(Optional.of(product));

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
        given(productRepository.findById(anyString())).willReturn(Optional.of(product));

        // when & then
        InsufficientStockException ex = catchThrowableOfType(
                InsufficientStockException.class,
                () -> productService.decreaseStock(product.getId(), quantityToDecrease)
        );

        then(ex).isNotNull();
        then(ex).isExactlyInstanceOf(InsufficientStockException.class);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void givenProductIdAndNegativeQuantity_whenDecreaseStock_thenThrowIllegalArgumentException() {
        // given
        int negativeQuantity = -10;
        Product product = ProductFactory.productWithStock(100);

        // when & then
        IllegalArgumentException ex = catchThrowableOfType(
                IllegalArgumentException.class,
                () -> productService.decreaseStock(product.getId(), negativeQuantity)
        );

        then(ex).isNotNull();
        verify(productRepository, never()).save(any(Product.class));
    }

}
