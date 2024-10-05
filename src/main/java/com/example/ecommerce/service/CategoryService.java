package com.example.ecommerce.service;

import com.example.ecommerce.exception.category.CategoryNotFoundException;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.mapper.PaginationMapper;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.payload.request.category.CreateCategoryRequest;
import com.example.ecommerce.payload.request.category.UpdateCategoryRequest;
import com.example.ecommerce.payload.response.CategoryResponse;
import com.example.ecommerce.payload.response.PaginatedResponse;
import com.example.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    private final PaginationMapper paginationMapper;


    protected Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    @Cacheable(value = "categories", key = "#pageable.pageNumber")
    public PaginatedResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        return paginationMapper.toPaginatedResponse(categoryRepository.findAll(pageable), categoryMapper);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(CreateCategoryRequest categoryRequest) {
        Category newCategory = new Category(0L, categoryRequest.categoryName());
        return categoryMapper.mapToResponse(categoryRepository.save(newCategory));
    }

    @CacheEvict(value = {"category", "categories"}, key = "#categoryId", allEntries = true)
    public CategoryResponse updateCategory(Long categoryId, UpdateCategoryRequest categoryRequest) {
        Category category = getCategoryById(categoryId);
        category.setName(categoryRequest.categoryName());
        return categoryMapper.mapToResponse(categoryRepository.save(category));
    }

    @CacheEvict(value = {"category", "categories"}, key = "#categoryId", allEntries = true)
    public void deleteCategory(Long categoryId) {
        categoryRepository.delete(getCategoryById(categoryId));
    }

}
