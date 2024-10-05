package com.example.ecommerce.model.listener;

import com.example.ecommerce.model.Product;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductListener {

    private static final Logger log = LoggerFactory.getLogger(ProductListener.class);

    @PrePersist
    public void beforeCreate(Product product) {
        refreshDiscount(product);
    }

    @PreUpdate
    public void beforeUpdate(Product product) {
        refreshDiscount(product);
    }

    private void refreshDiscount(Product product) {
        if (product.isDiscountActive() || product.isDiscountInFuture()) {
            log.info("Product '{}' has a valid discount", product.getName());
        } else {
            log.warn("Product '{}' has an invalid or expired discount", product.getName());
            product.setDiscount(null);
        }
    }

}
