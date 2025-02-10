package com.example.ecommerce.category.mapper;

import com.example.ecommerce.category.model.Category;
import com.example.ecommerce.category.payload.response.CategoryResponse;
import com.example.ecommerce.shared.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper extends GenericMapper<Category, CategoryResponse> {

    @Override
    @Mapping(target = "categoryId", source = "id")
    @Mapping(target = "categoryName", source = "name")
    CategoryResponse mapToResponse(Category category);

}
