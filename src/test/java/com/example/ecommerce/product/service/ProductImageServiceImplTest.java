package com.example.ecommerce.product.service;

import com.example.ecommerce.product.exception.EmptyFileException;
import com.example.ecommerce.product.exception.InvalidFileTypeException;
import com.example.ecommerce.product.exception.ProductImageNotFoundException;
import com.example.ecommerce.product.exception.ProductNotFoundException;
import com.example.ecommerce.product.factory.ProductImageFactory;
import com.example.ecommerce.product.model.ProductImage;
import com.example.ecommerce.product.payload.response.ProductImageResponse;
import com.example.ecommerce.product.repository.ProductImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductImageServiceImplTest {

    @InjectMocks
    ProductImageServiceImpl productImageService;

    @Mock
    ProductServiceImpl productService;

    @Mock
    ProductImageRepository productImageRepository;

    private String productId;
    private final String mockImageUrlTemplate = "localhost:8080/images/{filename}";

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID().toString();
    }

    @Test
    void givenProductIdAndValidMultipartFile_whenUploadProductImages_thenReturnSuccessfullyUploadedImageUrls() {
        // given
        MultipartFile[] files = {
                ProductImageFactory.validImageFile("image-1"),
                ProductImageFactory.validImageFile("image-2")
        };

        // when & then
        List<String> uploadedImageUrls = productImageService.uploadProductImages(
                productId,
                files,
                mockImageUrlTemplate
        );

        then(uploadedImageUrls).isNotNull();
        then(uploadedImageUrls).hasSize(files.length);
        then(uploadedImageUrls.stream().distinct().count()).isEqualTo(files.length);
        verify(productImageRepository, times(2)).save(any(ProductImage.class));
    }

    @Test
    void givenProductIdAndEmptyMultipartFile_whenUploadProductImages_thenThrowEmptyFileException() {
        // given
        MultipartFile[] files = new MultipartFile[]{ProductImageFactory.emptyImageFile()};

        // when & then
        EmptyFileException ex = catchThrowableOfType(
                EmptyFileException.class,
                () -> productImageService.uploadProductImages(productId, files, mockImageUrlTemplate)
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining(files[0].getOriginalFilename());
        verify(productImageRepository, never()).save(any(ProductImage.class));
    }

    @Test
    void givenProductIdAndUnsupportedMultipartFile_whenUploadProductImages_thenThrowInvalidFileTypeException() {
        // given
        MultipartFile[] files = new MultipartFile[]{ProductImageFactory.unsupportedFile()};

        // when & then
        InvalidFileTypeException ex = catchThrowableOfType(
                InvalidFileTypeException.class,
                () -> productImageService.uploadProductImages(productId, files, mockImageUrlTemplate)
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining(files[0].getOriginalFilename());
        verify(productImageRepository, never()).save(any(ProductImage.class));
    }


    @Test
    void givenInvalidProductIdAndMultipartFile_whenUploadProductImages_thenThrowProductNotFoundException() {
        // given
        MultipartFile[] files = new MultipartFile[]{ProductImageFactory.validImageFile()};
        given(productService.findProductEntityById(productId)).willThrow(new ProductNotFoundException(productId));

        // when & then
        ProductNotFoundException ex = catchThrowableOfType(
                ProductNotFoundException.class,
                () -> productImageService.uploadProductImages(productId, files, mockImageUrlTemplate)
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining(productId);
        verify(productImageRepository, never()).save(any());
        verify(productService, times(1)).findProductEntityById(productId);
    }

    @Test
    void givenProductIdAndFilename_whenProductImageFound_thenReturnProductImageResponse() {
        // given
        ProductImage productImage = ProductImageFactory.productImage();
        given(productImageRepository.findByProductIdAndFilename(any(), any())).willReturn(Optional.of(productImage));

        ProductImageResponse expected = new ProductImageResponse(productImage.getType(), productImage.getImageData());

        // when
        ProductImageResponse actual = productImageService.getProductImage(productImage.getId(), productImage.getFilename());

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        verify(productImageRepository, times(1)).findByProductIdAndFilename(
                productImage.getId(), productImage.getFilename()
        );
    }

    @Test
    void givenProductIdAndFilename_whenProductImageNotFound_thenThrowProductImageNotFoundException() {
        // given
        ProductImage productImage = ProductImageFactory.productImage();
        given(productImageRepository.findByProductIdAndFilename(any(), any())).willReturn(Optional.empty());

        // when & then
        ProductImageNotFoundException ex = catchThrowableOfType(
                ProductImageNotFoundException.class,
                () -> productImageService.getProductImage(productImage.getId(), productImage.getFilename())
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining(productImage.getFilename());
    }

    @Test
    void givenProductId_whenProductImagesFoundForProduct_thenReturnListOfUploadedImageUrlStrings() {
        // given
        List<ProductImage> productImages = ProductImageFactory.list(3, ProductImageFactory::productImage);
        given(productImageRepository.findAllByProductId(productId)).willReturn(productImages);

        // when
        List<String> uploadedImageUrls = productImageService.getAllProductImageUrls(productId);

        // then
        then(uploadedImageUrls).isNotNull();
        then(uploadedImageUrls).hasSize(productImages.size());
    }

    @Test
    void givenProductIdAndFilenames_whenProductImagesFound_thenDeleteProductImages() {
        // given
        Set<String> filenames = Set.of("image-1.jpg", "image-2.jpg", "image-3.jpg");

        int size = filenames.size();
        given(productImageRepository.countProductImageByProductId(any())).willReturn(size);
        for (int i = 0; i < size; i++) {
            given(productImageRepository.findByProductIdAndFilename(any(), anyString())).willReturn(Optional.of(new ProductImage()));
        }

        // when & then
        productImageService.deleteProductImages(productId, filenames);
        verify(productImageRepository, times(size)).delete(any(ProductImage.class));
    }

    @Test
    void givenProductIdAndFilenames_whenThereAreNoImagesToDelete_thenThrowProductImageNotFoundException() {
        // given
        given(productImageRepository.countProductImageByProductId(any())).willReturn(0);

        // when & then
        ProductImageNotFoundException ex = catchThrowableOfType(
                ProductImageNotFoundException.class,
                () -> productImageService.deleteProductImages(productId, Set.of())
        );

        then(ex).isNotNull();
        verify(productImageRepository, times(1)).countProductImageByProductId(any());
        verify(productImageRepository, never()).delete(any(ProductImage.class));
    }

}
