package com.example.ecommerce.category.mapper;

import com.example.ecommerce.category.model.Category;
import com.example.ecommerce.category.payload.response.CategoryResponse;
import com.example.ecommerce.shared.mapper.Mapper;
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
