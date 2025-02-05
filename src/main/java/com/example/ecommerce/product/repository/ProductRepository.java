package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findAllByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findAllByNameIgnoreCaseStartingWith(String name, Pageable pageable);

    @Query("SELECT p.stock FROM Product p WHERE p.id = ?1")
    Integer findStockQuantityByProductId(UUID productId);

}
