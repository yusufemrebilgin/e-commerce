package com.example.ecommerce.product.mapper;

import com.example.ecommerce.product.model.Product;
import com.example.ecommerce.product.model.ProductImage;
import com.example.ecommerce.product.model.embeddable.Discount;
import com.example.ecommerce.product.payload.request.UpdateProductRequest;
import com.example.ecommerce.product.payload.response.ProductResponse;
import com.example.ecommerce.shared.mapper.GenericMapper;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper extends GenericMapper<Product, ProductResponse> {

    @Override
    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "images", expression = "java(mapProductImagesToUrls(product.getImages()))")
    ProductResponse mapToResponse(Product product);

    default void updateProductFromRequest(@NonNull UpdateProductRequest request, @NonNull Product existingProduct) {
        existingProduct.setName(request.name());
        existingProduct.setDescription(request.description());
        existingProduct.setStock(request.stock());
        existingProduct.setPrice(request.price());

        Discount discount = existingProduct.getDiscount();
        if (discount == null) {
            discount = new Discount();
            existingProduct.setDiscount(discount);
        }

        if (request.discountPercentage() != null)
            discount.setPercentage(request.discountPercentage());
        if (request.discountStart() != null)
            discount.setStart(request.discountStart());
        if (request.discountEnd() != null)
            discount.setEnd(request.discountEnd());
    }

    default List<String> mapProductImagesToUrls(List<ProductImage> images) {
        return images.stream()
                .map(ProductImage::getUrl)
                .toList();
    }

}
