package com.example.ecommerce.cart.repository;

import com.example.ecommerce.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c WHERE c.user.username = ?1")
    Optional<Cart> findByUser(String username);

}
