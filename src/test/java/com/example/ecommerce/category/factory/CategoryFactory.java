package com.example.ecommerce.category.factory;

import com.example.ecommerce.category.model.Category;
import com.example.ecommerce.category.payload.response.CategoryResponse;

import java.util.ArrayList;
import java.util.List;

public final class CategoryFactory {

    private CategoryFactory() {
    }

    public static Category category(String name) {
        return category(1L, name);
    }

    public static Category category(Long id, String name) {
        return new Category(id, name);
    }

    public static List<Category> list(List<String> categoryNames) {
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < categoryNames.size(); i++) {
            categories.add(category((long) i, categoryNames.get(i)));
        }
        return categories;
    }

    public static CategoryResponse response(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }

    public static List<CategoryResponse> responseList(List<Category> categories) {
        return categories.stream().map(CategoryFactory::response).toList();
    }

}
