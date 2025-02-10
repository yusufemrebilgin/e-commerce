package com.example.ecommerce.cart.repository;

import com.example.ecommerce.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, String> {

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = ?1 AND ci.product.id = ?2")
    Optional<CartItem> findByCartIdAndProductId(Long cartId, String productId);

}
