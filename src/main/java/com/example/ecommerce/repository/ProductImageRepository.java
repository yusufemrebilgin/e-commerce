package com.example.ecommerce.repository;

import com.example.ecommerce.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

    List<ProductImage> findAllByProductId(UUID productId);

    boolean existsByFilename(String fileName);

    int countProductImageByProductId(UUID productId);

    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = ?1 AND pi.filename = ?2")
    Optional<ProductImage> findByProductIdAndFilename(UUID productId, String filename);

}
