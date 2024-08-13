package com.example.ecommerce.service;

import com.example.ecommerce.exception.CategoryNotFoundException;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.mapper.PaginationMapper;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.payload.dto.CategoryDto;
import com.example.ecommerce.payload.request.category.CreateCategoryRequest;
import com.example.ecommerce.payload.request.category.UpdateCategoryRequest;
import com.example.ecommerce.payload.response.PaginatedResponse;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.util.PageableFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    CategoryService categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    CategoryMapper categoryMapper;

    @Mock
    PaginationMapper paginationMapper;

    @Test
    void givenPaginationParameters_whenSuccess_returnPaginatedResponse() {
        // given
        int page = 0, size = 5;
        String sort = "name:asc";

        Pageable pageable = PageableFactory.getPageable(page, size, sort);

        Category category1 = new Category(1L, "Electronics");
        Category category2 = new Category(2L, "Computers");
        Category category3 = new Category(3L, "Clothing");

        List<CategoryDto> categoryDtoList = List.of(
                new CategoryDto(1L, "Electronics"),
                new CategoryDto(2L, "Computers"),
                new CategoryDto(3L, "Clothing")
        );

        Page<Category> categoryPage = new PageImpl<>(List.of(category1, category2, category3));

        PaginatedResponse<CategoryDto> expected = new PaginatedResponse<>(
                categoryDtoList,
                page,
                size,
                categoryPage.getTotalPages(),
                categoryPage.getNumberOfElements(),
                categoryPage.isLast()
        );

        given(categoryRepository.findAll(pageable)).willReturn(categoryPage);
        given(paginationMapper.toPaginatedResponse(categoryPage, categoryMapper)).willReturn(expected);

        // when
        PaginatedResponse<CategoryDto> actual = categoryService.getAllCategories(page, size, sort);

        // then
        then(actual).isNotNull();
        then(actual.content()).hasSize(3);
        then(actual.content().get(0).categoryName()).isEqualTo("Electronics");
        then(actual.content().get(1).categoryName()).isEqualTo("Computers");
        then(actual.content().get(2).categoryName()).isEqualTo("Clothing");

        verify(categoryRepository, times(1)).findAll(pageable);
        verify(paginationMapper, times(1)).toPaginatedResponse(categoryPage, categoryMapper);
    }

    @Test
    void givenCategoryId_whenCategoryFound_returnCategory() {
        // given
        Long categoryId = 1L;
        Category category = new Category(categoryId, "Electronics");
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        // when
        Category result = categoryService.getCategoryById(categoryId);

        // get
        then(result).isNotNull();
        then(result).isEqualTo(category);
        then(result.getId()).isEqualTo(categoryId);
        then(result.getName()).isEqualTo("Electronics");

    }

    @Test
    void givenCategoryId_whenCategoryNotFound_throwCategoryNotFoundException() {
        // given
        Long categoryId = 1L;
        given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

        // when
        CategoryNotFoundException ex = catchThrowableOfType(
                () -> categoryService.getCategoryById(categoryId),
                CategoryNotFoundException.class
        );

        // then
        then(ex).isNotNull();
        then(ex).hasMessageContaining(categoryId.toString());
    }

    @Test
    void givenCreateCategoryRequest_whenCreated_returnCategoryDto() {
        // given
        CreateCategoryRequest request = new CreateCategoryRequest("Electronics");
        Category category = new Category(0L, request.categoryName());
        CategoryDto categoryDto = new CategoryDto(0L, request.categoryName());

        given(categoryRepository.save(category)).willReturn(category);
        given(categoryMapper.mapToDto(category)).willReturn(categoryDto);

        // when
        CategoryDto result = categoryService.createCategory(request);

        // then
        then(result).isNotNull();
        then(result).isEqualTo(categoryDto);
        then(result.categoryName()).isEqualTo("Electronics");
    }

    @Test
    void givenUpdateCategoryRequest_whenUpdated_returnUpdatedCategoryDto() {
        //given
        Long categoryId = 1L;
        UpdateCategoryRequest request = new UpdateCategoryRequest("Electronics");

        Category existingCategory = new Category(categoryId, "Computers");
        Category updatedCategory = new Category(categoryId, "Electronics");
        CategoryDto categoryDto = new CategoryDto(categoryId, "Electronics");

        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(existingCategory));
        given(categoryRepository.save(any(Category.class))).willReturn(updatedCategory);
        given(categoryMapper.mapToDto(existingCategory)).willReturn(categoryDto);

        // when
        CategoryDto result = categoryService.updateCategory(categoryId, request);

        // then
        then(result).isNotNull();
        then(result.categoryName()).isEqualTo("Electronics");
    }

    @Test
    void givenCategoryId_whenSuccess_deleteCategory() {
        // given
        Long categoryId = 1L;
        Category category = new Category(categoryId, "Electronics");
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        // when
        categoryService.deleteCategory(categoryId);

        // then
        verify(categoryRepository, times(1)).delete(category);
    }

}