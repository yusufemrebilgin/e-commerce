package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ProductImage;
import com.example.ecommerce.payload.request.product.CreateProductRequest;
import com.example.ecommerce.payload.request.product.UpdateProductRequest;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper implements Mapper<Product, ProductDto> {

    @Override
    public ProductDto mapToDto(@NonNull Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getCategory().getName(),
                product.getDescription(),
                product.getStockQuantity(),
                product.getPrice(),
                product.getDiscountedPrice(),
                product.isDiscountAvailable(),
                product.getDiscountPercentage(),
                product.getDiscountStart(),
                product.getDiscountEnd(),
                product.getImages().isEmpty() ? Set.of() : product.getImages().stream()
                        .map(ProductImage::getUrl).collect(Collectors.toSet())
        );
    }

    public Product mapToEntity(@NonNull CreateProductRequest request) {
        return Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stockQuantity(request.quantity())
                .discountPercentage(request.discountPercentage())
                .discountStart(request.discountStart())
                .discountEnd(request.discountEnd())
                .images(List.of())
                .build();
    }

    public void updateProductFromDto(@NonNull UpdateProductRequest request, @NonNull Product existingProduct) {
        existingProduct.setName(request.name());
        existingProduct.setDescription(request.description());
        existingProduct.setPrice(request.price());
        existingProduct.setStockQuantity(request.quantity());

        // Update discount related fields if they are provided
        if (request.discountPercentage() != null)
            existingProduct.setDiscountPercentage(request.discountPercentage());
        if (request.discountStart() != null)
            existingProduct.setDiscountStart(request.discountStart());
        if (request.discountEnd() != null)
            existingProduct.setDiscountEnd(request.discountEnd());
    }

}
