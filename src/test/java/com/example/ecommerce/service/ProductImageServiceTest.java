package com.example.ecommerce.service;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.product.ProductImageNotFoundException;
import com.example.ecommerce.exception.product.ProductNotFoundException;
import com.example.ecommerce.factory.ProductImageFactory;
import com.example.ecommerce.model.ProductImage;
import com.example.ecommerce.payload.response.ProductImageResponse;
import com.example.ecommerce.repository.ProductImageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductImageServiceTest {

    @InjectMocks
    ProductImageService productImageService;

    @Mock
    ProductService productService;

    @Mock
    ProductImageRepository productImageRepository;

    private final String mockImageUrlTemplate = "localhost:8080/images/{filename}";

    @Test
    void givenProductIdMultipartFile_whenProductExistsAndFileIsValid_thenUploadProductImage() {
        // given
        MultipartFile[] files = new MultipartFile[]{ProductImageFactory.validImageFile()};

        // when & then
        productImageService.uploadProductImages(UUID.randomUUID(), files, mockImageUrlTemplate);
        verify(productImageRepository, times(1)).save(any(ProductImage.class));
    }


    @Test
    void givenProductIdAndMultipartFile_whenUploadedFileIsEmpty_throwMultipartException() {
        // given
        MultipartFile[] files = new MultipartFile[]{ProductImageFactory.emptyImageFile()};

        // when & then
        MultipartException ex = catchThrowableOfType(
                () -> productImageService.uploadProductImages(UUID.randomUUID(), files, mockImageUrlTemplate),
                MultipartException.class
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining("Uploaded file is empty");
        verify(productImageRepository, never()).save(any(ProductImage.class));
    }

    @Test
    void givenProductIdAndMultipartFile_whenUploadedMimeTypeIsUnsupported_throwMultipartException() {
        // given
        MultipartFile[] files = new MultipartFile[]{ProductImageFactory.unsupportedFile()};

        // when & then
        MultipartException ex = catchThrowableOfType(
                () -> productImageService.uploadProductImages(UUID.randomUUID(), files, mockImageUrlTemplate),
                MultipartException.class
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining("Invalid file type for %s", files[0].getOriginalFilename());
        verify(productImageRepository, never()).save(any(ProductImage.class));
    }

    @Test
    void givenProductIdAndMultipartFile_whenProductNotExists_throwProductNotFoundException() {
        // given
        UUID productId = UUID.randomUUID();
        MultipartFile[] files = new MultipartFile[]{ProductImageFactory.validImageFile()};
        given(productService.findProductById(any(UUID.class))).willThrow(new ProductNotFoundException(productId));

        // when & then
        ProductNotFoundException ex = catchThrowableOfType(
                () -> productImageService.uploadProductImages(productId, files, mockImageUrlTemplate),
                ProductNotFoundException.class
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining(ErrorMessages.PRODUCT_NOT_FOUND.message(productId));
        verify(productService, times(1)).findProductById(any(UUID.class));
        verify(productImageRepository, never()).save(any());
    }

    @Test
    void givenProductIdAndFilename_whenProductImageFound_returnProductImageResponse() {
        // given
        ProductImage productImage = ProductImage.builder()
                .filename("image.jpg")
                .type("image/jpeg")
                .imageData("image-data".getBytes())
                .build();

        given(productImageRepository.findByProductIdAndFilename(any(UUID.class), anyString())).willReturn(Optional.of(productImage));

        ProductImageResponse expected = new ProductImageResponse(productImage.getType(), productImage.getImageData());

        // when
        ProductImageResponse actual = productImageService.getProductImage(UUID.randomUUID(), productImage.getFilename());

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        verify(productImageRepository, times(1)).findByProductIdAndFilename(any(UUID.class), anyString());
    }

    @Test
    void givenProductIdAndFilename_whenProductImageNotFound_throwProductImageNotFoundException() {
        // given
        String filename = "image.jpg";
        given(productImageRepository.findByProductIdAndFilename(any(UUID.class), anyString())).willReturn(Optional.empty());

        // when & then
        ProductImageNotFoundException ex = catchThrowableOfType(
                () -> productImageService.getProductImage(UUID.randomUUID(), filename),
                ProductImageNotFoundException.class
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining(ErrorMessages.PRODUCT_IMAGE_NOT_FOUND.message(filename));
    }

    @Test
    void givenProductId_whenProductImagesFound_returnAllImageUrls() {
        // given
        String filename = "product-image.jpg";
        String imageUrl = mockImageUrlTemplate.replace("{filename}", filename);

        ProductImage image = ProductImage.builder()
                .filename(filename)
                .url(imageUrl)
                .build();

        List<Map<String, String>> expected = List.of(Map.of("url", imageUrl));

        given(productImageRepository.findAllByProductId(any(UUID.class))).willReturn(List.of(image));

        // when
        List<Map<String, String>> actual = productImageService.getAllProductImages(UUID.randomUUID());

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(actual.get(0).get("url")).isEqualTo(imageUrl);
    }

    @Test
    void givenProductIdAndFilenames_whenProductImagesFound_thenDeleteProductImages() {
        // given
        Set<String> filenames = Set.of("image-1.jpg", "image-2.jpg", "image-3.jpg");
        given(productImageRepository.countProductImageByProductId(any())).willReturn(3);
        for (int i = 0; i < 3; i++) {
            given(productImageRepository.findByProductIdAndFilename(any(), anyString())).willReturn(Optional.of(new ProductImage()));
        }

        // when & then
        productImageService.deleteProductImages(UUID.randomUUID(), filenames);
        verify(productImageRepository, times(3)).delete(any(ProductImage.class));
    }

    @Test
    void givenProductIdAndFilenames_whenThereAreNoImagesToDelete_throwProductImageNotFoundException() {
        // given
        given(productImageRepository.countProductImageByProductId(any())).willReturn(0);

        // when & then
        ProductImageNotFoundException ex = catchThrowableOfType(
                () -> productImageService.deleteProductImages(UUID.randomUUID(), Set.of()),
                ProductImageNotFoundException.class
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining("There are no images to delete");
        verify(productImageRepository, times(1)).countProductImageByProductId(any());
        verify(productImageRepository, never()).delete(any(ProductImage.class));
    }

}
