package com.example.ecommerce.product.model.listener;

import com.example.ecommerce.product.model.Product;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entity listener for the {@link Product} entity.
 * This listener is responsible for handling events such as creation and update
 * of a product to ensure proper discount handling.
 */
public class ProductListener {

    private static final Logger log = LoggerFactory.getLogger(ProductListener.class);

    /**
     * Invoked before a new {@link Product} entity is persisted to the database.
     * Ensures that any applicable discount is refreshed and valid before creation.
     *
     * @param product the {@link Product} entity being created
     */
    @PrePersist
    public void beforeCreate(Product product) {
        refreshDiscount(product);
    }

    /**
     * Invoked before an existing {@link Product} entity is updated in the database.
     * Ensures that any applicable discount is refreshed and valid before the update.
     *
     * @param product the {@link Product} entity being updated
     */
    @PreUpdate
    public void beforeUpdate(Product product) {
        refreshDiscount(product);
    }

    /**
     * Refreshes the discount status of a given {@link Product}.
     * If the product has a valid discount, it keeps the discount and logs an informational message.
     * If the discount is invalid or expired, it resets the discount and logs a warning message.
     */
    private void refreshDiscount(Product product) {
        if (product.getDiscount() != null && !product.isDiscountExpired()) {
            log.info("Product '{}' has a valid discount", product.getName());
        } else {
            log.warn("Product '{}' has an invalid or expired discount", product.getName());
            log.info("Expired discount removed for product: {}", product.getName());
            product.resetDiscount();
        }
    }

}
