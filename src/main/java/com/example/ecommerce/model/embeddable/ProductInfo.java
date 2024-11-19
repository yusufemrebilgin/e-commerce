package com.example.ecommerce.model.embeddable;

import com.example.ecommerce.model.Product;
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
public class ProductInfo {

    private int quantity;
    private BigDecimal unitPrice = BigDecimal.ZERO;
    private BigDecimal totalPrice = BigDecimal.ZERO;

    public static ProductInfo calculateProductInfo(int quantity, Product product) {
        BigDecimal unitPrice = product.getPrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return new ProductInfo(quantity, unitPrice, totalPrice);
    }

}
