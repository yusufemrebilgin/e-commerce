package com.example.ecommerce.category.service;

import com.example.ecommerce.category.exception.CategoryNotFoundException;
import com.example.ecommerce.category.mapper.CategoryMapper;
import com.example.ecommerce.category.model.Category;
import com.example.ecommerce.category.payload.request.CreateCategoryRequest;
import com.example.ecommerce.category.payload.request.UpdateCategoryRequest;
import com.example.ecommerce.category.payload.response.CategoryResponse;
import com.example.ecommerce.category.repository.CategoryRepository;
import com.example.ecommerce.shared.payload.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public Category findCategoryEntityById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    @Override
    @Cacheable(value = "categories", key = "#pageable.pageNumber")
    public PaginatedResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryMapper.mapToPaginatedResponse(categoryRepository.findAll(pageable));
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(CreateCategoryRequest createRequest) {
        Category newCategory = new Category(0L, createRequest.name());
        return categoryMapper.mapToResponse(categoryRepository.save(newCategory));
    }

    @Override
    @CacheEvict(value = {"category", "categories"}, key = "#categoryId", allEntries = true)
    public CategoryResponse updateCategory(Long categoryId, UpdateCategoryRequest updateRequest) {
        Category existingCategory = findCategoryEntityById(categoryId);
        existingCategory.setName(updateRequest.categoryName());
        return categoryMapper.mapToResponse(categoryRepository.save(existingCategory));
    }

    @Override
    @CacheEvict(value = {"category", "categories"}, key = "#categoryId", allEntries = true)
    public void deleteCategory(Long categoryId) {
        categoryRepository.delete(findCategoryEntityById(categoryId));
    }

}
