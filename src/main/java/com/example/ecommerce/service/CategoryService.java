package com.example.ecommerce.service;

import com.example.ecommerce.exception.category.CategoryNotFoundException;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.mapper.PaginationMapper;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.payload.dto.CategoryDto;
import com.example.ecommerce.payload.request.category.CreateCategoryRequest;
import com.example.ecommerce.payload.request.category.UpdateCategoryRequest;
import com.example.ecommerce.payload.response.PaginatedResponse;
import com.example.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final PaginationMapper paginationMapper;

    protected Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    public PaginatedResponse<CategoryDto> getAllCategories(Pageable pageable) {
        return paginationMapper.toPaginatedResponse(categoryRepository.findAll(pageable), categoryMapper);
    }

    public CategoryDto createCategory(CreateCategoryRequest categoryRequest) {
        Category newCategory = new Category(0L, categoryRequest.categoryName());
        return categoryMapper.mapToDto(categoryRepository.save(newCategory));
    }

    public CategoryDto updateCategory(Long categoryId, UpdateCategoryRequest categoryRequest) {
        Category category = getCategoryById(categoryId);
        category.setName(categoryRequest.categoryName());
        return categoryMapper.mapToDto(categoryRepository.save(category));
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.delete(getCategoryById(categoryId));
    }

}
