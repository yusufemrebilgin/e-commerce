package com.example.ecommerce.mapper;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ProductImage;
import com.example.ecommerce.payload.response.ProductResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper implements Mapper<Product, ProductResponse> {

    @Override
    public ProductResponse mapToResponse(@NonNull Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory().getName(),
                product.getDescription(),
                product.getStock(),
                product.getPrice(),
                product.getDiscount(),
                product.getImages().stream().map(ProductImage::getUrl).toList()
        );
    }

}
