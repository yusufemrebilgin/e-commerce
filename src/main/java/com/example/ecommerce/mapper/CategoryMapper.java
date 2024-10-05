package com.example.ecommerce.mapper;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.payload.response.CategoryResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper implements Mapper<Category, CategoryResponse> {

    @Override
    public CategoryResponse mapToResponse(@NonNull Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }

}
