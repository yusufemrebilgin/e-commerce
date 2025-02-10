package com.example.ecommerce.cart.model.embeddable;

import com.example.ecommerce.product.model.Product;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class DiscountInfo {

    private boolean discountApplied;
    private BigDecimal discountPerItem = BigDecimal.ZERO;
    private BigDecimal totalDiscountAmount = BigDecimal.ZERO;
    private BigDecimal discountedUnitPrice = BigDecimal.ZERO;
    private BigDecimal discountedTotalPrice = BigDecimal.ZERO;

    public static DiscountInfo noDiscount() {
        return new DiscountInfo();
    }

    public static DiscountInfo calculateDiscountInfo(int quantity, Product product) {
        if (product == null || !product.isDiscountActive()) {
            return DiscountInfo.noDiscount();
        }

        // Convert quantity value to BigDecimal once to avoid repeated conversion
        BigDecimal quantityBD = BigDecimal.valueOf(quantity);

        // Calculate prices and discount amounts
        BigDecimal unitPrice = product.getPrice(); // Original price for given product
        BigDecimal discountedUnitPrice = product.getDiscountedPrice();
        BigDecimal discountedTotalPrice = discountedUnitPrice.multiply(quantityBD);
        BigDecimal discountAmountPerItem = unitPrice.subtract(discountedUnitPrice);
        BigDecimal totalDiscountAmount = discountAmountPerItem.multiply(quantityBD);

        return new DiscountInfo(
                true,
                discountAmountPerItem,
                totalDiscountAmount,
                discountedUnitPrice,
                discountedTotalPrice
        );
    }

}
