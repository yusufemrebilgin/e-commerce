package com.example.ecommerce.service;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.cart.InsufficientStockException;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    void givenPaginationParameters_whenGetAllProducts_returnPaginatedProductResponse(int page, int size, int expectedSize) {
        // given
        List<Product> products = ProductFactory.list(3, ProductFactory::product);
        Page<Product> productPage = new PageImpl<>(
                products.subList(page * size, Math.min((page + 1) * size, products.size()))
        );

        List<ProductResponse> productResponses = ProductFactory.responseList(products);

        PaginatedResponse<ProductResponse> expected = new PaginatedResponse<>(
                productResponses.subList(0, expectedSize),
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
        Product expected = ProductFactory.product();
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(expected));

        // when
        Product actual = productService.findProductById(expected.getId());

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(actual.getId()).isEqualTo(expected.getId());
    }

    @Test
    void givenProductId_whenProductNotFound_throwProductNotFoundException() {
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
        then(ex.getMessage()).isEqualTo(ErrorMessages.PRODUCT_NOT_FOUND.message(productId));
    }

    @Test
    void givenCreateProductRequest_whenCreated_returnProductResponse() {
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
        then(actual.category()).isEqualTo(product.getCategory().getName());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).mapToResponse(any(Product.class));
    }

    @Test
    void givenUpdateProductRequest_whenProductExists_returnUpdatedProductResponse() {
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
    void givenProductId_whenProductExists_deleteProduct() {
        // given
        Product product = ProductFactory.product();
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        // when & then
        productService.deleteProduct(product.getId());
        verify(productRepository, times(1)).delete(product);
        verify(productRepository).delete(argThat(p -> p.getId().equals(product.getId())));
    }

    @Test
    void givenProductIdAndValidQuantity_whenSufficientStock_doNothing() {
        // given
        UUID productId = UUID.randomUUID();
        given(productRepository.findStockQuantityByProductId(productId)).willReturn(500);

        // when & then
        productService.checkStock(productId, 500);
        verify(productRepository, times(1)).findStockQuantityByProductId(productId);
    }

    @Test
    void givenProductIdAndRequestedQuantity_whenInsufficientStock_throwInsufficientStockException() {
        // given
        UUID productId = UUID.randomUUID();
        given(productRepository.findStockQuantityByProductId(productId)).willReturn(500);

        // when & then
        InsufficientStockException ex = catchThrowableOfType(
                () -> productService.checkStock(productId, 1_000),
                InsufficientStockException.class
        );

        then(ex).isNotNull();
        then(ex.getMessage()).isEqualTo(ErrorMessages.INSUFFICIENT_STOCK.message(500, 1_000));
    }

    @Test
    void givenProductIdAndQuantity_whenProductHasSufficientStock_thenDecreaseStock() {
        // given
        int quantity = 500;
        int currentStock = 1_000;
        Product product = ProductFactory.productWithStock(currentStock);
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(product));

        // when
        productService.decreaseStock(product.getId(), quantity);

        // then
        then(product).isNotNull();
        then(product.getStock()).isEqualTo(currentStock - quantity);
        verify(productRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void givenProductIdAndQuantity_whenProductHasInsufficientStock_throwInsufficientStockException() {
        // given
        int quantity = 500;
        int currentStock = 499;
        Product product = ProductFactory.productWithStock(currentStock);
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(product));

        // when & then
        InsufficientStockException ex = catchThrowableOfType(
                () -> productService.decreaseStock(product.getId(), quantity),
                InsufficientStockException.class
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining("Not enough stock");
    }

}
