package com.example.ecommerce.category.controller;

import com.example.ecommerce.category.payload.request.CreateCategoryRequest;
import com.example.ecommerce.category.payload.request.UpdateCategoryRequest;
import com.example.ecommerce.category.payload.response.CategoryResponse;
import com.example.ecommerce.category.service.CategoryService;
import com.example.ecommerce.shared.payload.PaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Retrieves all categories with pagination.
     *
     * @param pageable pagination information
     * @return a {@link ResponseEntity} containing a {@link PaginatedResponse} of {@link CategoryResponse}
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<CategoryResponse>> getAllCategories(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageable));
    }

    /**
     * Creates a new category.
     *
     * @param request the {@link CreateCategoryRequest} containing category details to be created
     * @return a {@link ResponseEntity} containing the created {@link CategoryResponse}
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    /**
     * Updates an existing category.
     *
     * @param categoryId the unique identifier of the category to be updated
     * @param request the {@link UpdateCategoryRequest} containing the updated details
     * @return a {@link ResponseEntity} containing the updated {@link CategoryResponse}
     */
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long categoryId, @Valid @RequestBody UpdateCategoryRequest request
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, request));
    }

    /**
     * Deletes a category.
     *
     * @param categoryId the unique identifier of the category to be deleted
     * @return a {@link ResponseEntity} with no content indicating the category has been deleted
     */
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

}
