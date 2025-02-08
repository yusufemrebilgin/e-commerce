package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, String> {

    List<ProductImage> findAllByProductId(String productId);

    boolean existsByFilename(String fileName);

    int countProductImageByProductId(String productId);

    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = ?1 AND pi.filename = ?2")
    Optional<ProductImage> findByProductIdAndFilename(String productId, String filename);

}
