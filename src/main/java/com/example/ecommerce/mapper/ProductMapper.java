package com.example.ecommerce.mapper;

import com.example.ecommerce.model.embeddable.Discount;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ProductImage;
import com.example.ecommerce.payload.request.product.UpdateProductRequest;
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

    public void updateProductFromRequest(@NonNull UpdateProductRequest request, @NonNull Product existingProduct) {
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

}
