package com.example.ecommerce.category.repository;

import com.example.ecommerce.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {}
