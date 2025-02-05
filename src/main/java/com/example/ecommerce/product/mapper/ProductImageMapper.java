package com.example.ecommerce.product.mapper;

import com.example.ecommerce.product.model.ProductImage;
import com.example.ecommerce.product.payload.response.ProductImageResponse;
import com.example.ecommerce.shared.mapper.Mapper;
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
