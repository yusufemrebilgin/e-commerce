package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.payload.request.category.CreateCategoryRequest;
import com.example.ecommerce.payload.request.category.UpdateCategoryRequest;
import com.example.ecommerce.payload.response.PaginatedResponse;
import com.example.ecommerce.service.CategoryService;
import com.example.ecommerce.util.PageableFactory;
import com.example.ecommerce.util.URIBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<CategoryDto>> getAllCategories(
            @RequestParam(defaultValue = PageableFactory.DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = PageableFactory.DEFAULT_SIZE) int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(categoryService.getAllCategories(page, size, sort));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryDto createdCategory = categoryService.createCategory(request);
        return ResponseEntity
                .created(URIBuilder.getResourceLocation(createdCategory.categoryId()))
                .body(createdCategory);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId,
                                            @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, request));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

}
