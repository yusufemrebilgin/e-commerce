package com.example.ecommerce.category.service;

import com.example.ecommerce.category.exception.CategoryNotFoundException;
import com.example.ecommerce.category.mapper.CategoryMapper;
import com.example.ecommerce.shared.mapper.PaginationMapper;
import com.example.ecommerce.category.model.Category;
import com.example.ecommerce.category.payload.request.CreateCategoryRequest;
import com.example.ecommerce.category.payload.request.UpdateCategoryRequest;
import com.example.ecommerce.category.payload.response.CategoryResponse;
import com.example.ecommerce.shared.payload.PaginatedResponse;
import com.example.ecommerce.category.repository.CategoryRepository;
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

    /**
     * Retrieves a category by its ID.
     *
     * @param categoryId ID of the category to retrieve
     * @return found {@link Category}
     * @throws CategoryNotFoundException if category is not found
     */
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    /**
     * Retrieves a paginated list of all categories. Caches the result to improve performance.
     *
     * @param pageable pagination information
     * @return a paginated list of {@link CategoryResponse}
     */
    @Cacheable(value = "categories", key = "#pageable.pageNumber")
    public PaginatedResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        return paginationMapper.toPaginatedResponse(categoryRepository.findAll(pageable), categoryMapper);
    }

    /**
     * Creates a new category and evicts the category cache to ensure data consistency.
     *
     * @param categoryRequest a {@link CreateCategoryRequest} containing new category data
     * @return newly created {@link CategoryResponse}
     */
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(CreateCategoryRequest categoryRequest) {
        Category newCategory = new Category(0L, categoryRequest.name());
        return categoryMapper.mapToResponse(categoryRepository.save(newCategory));
    }

    /**
     * Updates an existing category and evicts the relevant caches to ensure updated data is fetched.
     *
     * @param categoryId      ID of the category to update
     * @param categoryRequest a {@link UpdateCategoryRequest} containing updated category data
     * @return updated {@link CategoryResponse}
     * @throws CategoryNotFoundException if category with given ID is not found
     */
    @CacheEvict(value = {"category", "categories"}, key = "#categoryId", allEntries = true)
    public CategoryResponse updateCategory(Long categoryId, UpdateCategoryRequest categoryRequest) {
        Category existingCategory = getCategoryById(categoryId);
        existingCategory.setName(categoryRequest.categoryName());
        return categoryMapper.mapToResponse(categoryRepository.save(existingCategory));
    }

    /**
     * Deletes a category by its ID and evicts the relevant caches.
     *
     * @param categoryId ID of the category data
     * @throws CategoryNotFoundException if category with given ID is not found
     */
    @CacheEvict(value = {"category", "categories"}, key = "#categoryId", allEntries = true)
    public void deleteCategory(Long categoryId) {
        categoryRepository.delete(getCategoryById(categoryId));
    }

}
