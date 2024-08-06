package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.ProductImageDto;
import com.example.ecommerce.model.ProductImage;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ProductImageMapper implements Mapper<ProductImage, ProductImageDto> {

    @Override
    public ProductImageDto mapToDto(@NonNull ProductImage productImage) {
        return new ProductImageDto(
                productImage.getFilename(),
                productImage.getUrl()
        );
    }

}
