package com.example.ecommerce.category.service;

import com.example.ecommerce.category.exception.CategoryNotFoundException;
import com.example.ecommerce.category.model.Category;
import com.example.ecommerce.category.payload.request.CreateCategoryRequest;
import com.example.ecommerce.category.payload.request.UpdateCategoryRequest;
import com.example.ecommerce.category.payload.response.CategoryResponse;
import com.example.ecommerce.shared.payload.PaginatedResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing categories.
 */
public interface CategoryService {

    /**
     * Retrieves a category entity by ID.
     *
     * @param categoryId the category ID
     * @return the found {@link Category}
     * @throws CategoryNotFoundException if the category is not found
     */
    Category findCategoryEntityById(Long categoryId);

    /**
     * Retrieves a paginated list of categories.
     *
     * @param pageable pagination details
     * @return a paginated response of {@link CategoryResponse}
     */
    PaginatedResponse<CategoryResponse> getAllCategories(Pageable pageable);

    /**
     * Creates a new category.
     *
     * @param createRequest the request containing category details
     * @return the created {@link CategoryResponse}
     */
    CategoryResponse createCategory(CreateCategoryRequest createRequest);

    /**
     * Updates an existing category.
     *
     * @param categoryId    the category ID
     * @param updateRequest the request containing updated details
     * @return the updated {@link CategoryResponse}
     * @throws CategoryNotFoundException if the category is not found
     */
    CategoryResponse updateCategory(Long categoryId, UpdateCategoryRequest updateRequest);

    /**
     * Deletes a category by ID.
     *
     * @param categoryId the category ID
     * @throws CategoryNotFoundException if the category is not found
     */
    void deleteCategory(Long categoryId);

}
