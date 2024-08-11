package com.example.ecommerce.mapper;

import com.example.ecommerce.payload.dto.CategoryDto;
import com.example.ecommerce.model.Category;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper implements Mapper<Category, CategoryDto> {

    @Override
    public CategoryDto mapToDto(@NonNull Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
    
}
