package com.example.ecommerce.product.mapper;

import com.example.ecommerce.product.model.ProductImage;
import com.example.ecommerce.product.payload.response.ProductImageResponse;
import com.example.ecommerce.shared.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductImageMapper extends GenericMapper<ProductImage, ProductImageResponse> {

    @Override
    @Mapping(source = "type", target = "contentType")
    ProductImageResponse mapToResponse(ProductImage productImage);

}
