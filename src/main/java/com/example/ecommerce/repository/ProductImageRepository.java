package com.example.ecommerce.repository;

import com.example.ecommerce.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

    Optional<ProductImage> findByFilename(String filename);
    boolean existsByFilename(String fileName);

}
