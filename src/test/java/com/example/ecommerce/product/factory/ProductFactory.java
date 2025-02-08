package com.example.ecommerce.product.factory;

import com.example.ecommerce.category.factory.CategoryFactory;
import com.example.ecommerce.product.model.Product;
import com.example.ecommerce.product.model.ProductImage;
import com.example.ecommerce.product.model.embeddable.Discount;
import com.example.ecommerce.product.payload.request.CreateProductRequest;
import com.example.ecommerce.product.payload.request.UpdateProductRequest;
import com.example.ecommerce.product.payload.response.ProductResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class ProductFactory {

    private ProductFactory() {
    }

    public static Product product() {
        return product("default-id", "Default Product");
    }

    public static Product product(String id, String name) {
        return Product.builder()
                .id(id)
                .name(name)
                .description("Description for test product")
                .price(BigDecimal.valueOf(1000))
                .stock(10)
                .category(CategoryFactory.category("test-category"))
                .build();
    }

    public static Product productWithStock(int stock) {
        return Product.builder()
                .id("default-id")
                .name("Custom Product with Stock Value")
                .description("Product with given stock: " + stock)
                .price(BigDecimal.valueOf(1000))
                .stock(stock)
                .category(CategoryFactory.category("test-category"))
                .build();
    }

    public static List<Product> list(int size, Supplier<Product> productSupplier) {
        List<Product> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(productSupplier.get());
        }
        return list;
    }

    public static ProductResponse response(Product product) {
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

    public static List<ProductResponse> responseList(List<Product> products) {
        return products.stream().map(ProductFactory::response).toList();
    }

    public static CreateProductRequest createRequest(Product product) {
        Discount discount = Objects.requireNonNullElse(product.getDiscount(), new Discount());
        return new CreateProductRequest(
                product.getName(),
                product.getDescription(),
                product.getCategory().getId(),
                product.getStock(),
                product.getPrice(),
                discount.getPercentage(),
                discount.getStart(),
                discount.getEnd()
        );
    }

    public static UpdateProductRequest updateRequest(Product product) {
        Discount discount = Objects.requireNonNullElse(product.getDiscount(), new Discount());
        return new UpdateProductRequest(
                product.getName(),
                product.getDescription(),
                product.getCategory().getId(),
                product.getStock(),
                product.getPrice(),
                discount.getPercentage(),
                discount.getStart(),
                discount.getEnd()
        );
    }

}
