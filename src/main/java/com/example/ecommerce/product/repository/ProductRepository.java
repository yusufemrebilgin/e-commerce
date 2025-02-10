package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findAllByCategoryNameIgnoreCase(String categoryName, Pageable pageable);

    Page<Product> findAllByNameIgnoreCaseStartingWith(String name, Pageable pageable);

    @Query("SELECT p.stock FROM Product p WHERE p.id = ?1")
    Integer findStockQuantityByProductId(String productId);

}
