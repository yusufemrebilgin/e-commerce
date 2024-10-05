package com.example.ecommerce.mapper;

import com.example.ecommerce.model.ProductImage;
import com.example.ecommerce.payload.response.ProductImageResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ProductImageMapper implements Mapper<ProductImage, ProductImageResponse> {

    @Override
    public ProductImageResponse mapToResponse(@NonNull ProductImage productImage) {
        return new ProductImageResponse(
                productImage.getType(),
                productImage.getImageData()
        );
    }

}
