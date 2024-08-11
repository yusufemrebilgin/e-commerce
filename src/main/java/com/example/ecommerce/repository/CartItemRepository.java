package com.example.ecommerce.repository;

import com.example.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    @Query("SELECT CASE WHEN COUNT(ci) > 0 THEN TRUE ELSE FALSE END FROM CartItem ci WHERE ci.cart.id = ?1 AND ci.product.id = ?2")
    boolean existsByCartIdAndProductId(Long cartId, UUID productId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = ?1 AND ci.product.id = ?2")
    CartItem findByCartIdAndProductId(Long cartId, UUID productId);

}
