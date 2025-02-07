package com.example.ecommerce.category.service;

import com.example.ecommerce.category.factory.CategoryFactory;
import com.example.ecommerce.category.exception.CategoryNotFoundException;
import com.example.ecommerce.category.mapper.CategoryMapper;
import com.example.ecommerce.category.model.Category;
import com.example.ecommerce.category.payload.request.CreateCategoryRequest;
import com.example.ecommerce.category.payload.request.UpdateCategoryRequest;
import com.example.ecommerce.category.payload.response.CategoryResponse;
import com.example.ecommerce.category.repository.CategoryRepository;
import com.example.ecommerce.shared.payload.PaginatedResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    CategoryMapper categoryMapper;

    @ParameterizedTest
    @CsvSource({
            "0, 5, 3",
            "0, 2, 2",
            "1, 2, 1",
            "1, 3, 0"
    })
    void givenPaginationParameters_whenSuccess_thenReturnPaginatedResponse(int page, int size, int expectedSize) {
        // given
        List<Category> categories = CategoryFactory.list(List.of("Clothing", "Computers", "Electronics"));

        // from: page * size
        // to  : (page + 1) * size or categories.size()
        Page<Category> categoryPage = new PageImpl<>(categories.subList(page * size, Math.min((page + 1) * size, categories.size())));

        List<CategoryResponse> response = CategoryFactory.responseList(categories);
        List<CategoryResponse> expectedResponseList = response.subList(0, expectedSize);

        PaginatedResponse<CategoryResponse> expected = new PaginatedResponse<>(
                expectedResponseList,
                page,
                size,
                categoryPage.getTotalPages(),
                categoryPage.getNumberOfElements(),
                categoryPage.isLast()
        );

        given(categoryRepository.findAll(any(Pageable.class))).willReturn(categoryPage);
        given(categoryMapper.mapToPaginatedResponse(categoryPage)).willReturn(expected);

        // when
        PaginatedResponse<CategoryResponse> actual = categoryService.getAllCategories(PageRequest.of(page, size));

        // then
        then(actual).isNotNull();
        then(actual.page()).isEqualTo(page);
        then(actual.size()).isEqualTo(size);
        then(actual.content()).hasSize(expectedSize);
        then(actual.totalPages()).isEqualTo(categoryPage.getTotalPages());
        for (int i = 0; i < expectedSize; i++) {
            then(actual.content().get(i).categoryName()).isEqualTo(expectedResponseList.get(i).categoryName());
        }

        verify(categoryRepository, times(1)).findAll(any(Pageable.class));
        verify(categoryMapper, times(1)).mapToPaginatedResponse(categoryPage);
    }

    @Test
    void givenCategoryId_whenCategoryFound_thenReturnCategory() {
        // given
        Category expected = CategoryFactory.category("Electronics");
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(expected));

        // when
        Category actual = categoryService.findCategoryEntityById(expected.getId());

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
    }

    @Test
    void givenCategoryId_whenCategoryNotFound_thenThrowCategoryNotFoundException() {
        // given
        Long categoryId = 1L;
        given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

        // when
        CategoryNotFoundException ex = catchThrowableOfType(
                CategoryNotFoundException.class,
                () -> categoryService.findCategoryEntityById(categoryId)
        );

        // then
        then(ex).isNotNull();
        then(ex).hasMessageContaining(categoryId.toString());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void givenCreateCategoryRequest_whenCategoryCreated_thenReturnCategoryResponse() {
        // given
        CreateCategoryRequest request = new CreateCategoryRequest("Electronics");
        Category category = CategoryFactory.category(request.name());
        CategoryResponse expected = CategoryFactory.response(category);

        given(categoryRepository.save(any(Category.class))).willReturn(category);
        given(categoryMapper.mapToResponse(category)).willReturn(expected);

        // when
        CategoryResponse actual = categoryService.createCategory(request);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
    }

    @Test
    void givenUpdateCategoryRequest_whenCategoryUpdated_thenReturnUpdatedCategoryResponse() {
        //given
        UpdateCategoryRequest request = new UpdateCategoryRequest("Electronics");

        Category existingCategory = CategoryFactory.category("Computers");
        Category updatedCategory = CategoryFactory.category(request.categoryName());

        CategoryResponse expected = CategoryFactory.response(updatedCategory);

        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(existingCategory));
        given(categoryRepository.save(any(Category.class))).willReturn(updatedCategory);
        given(categoryMapper.mapToResponse(updatedCategory)).willReturn(expected);

        // when
        CategoryResponse actual = categoryService.updateCategory(existingCategory.getId(), request);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
    }

    @Test
    void givenCategoryId_whenCategoryExists_thenDeleteCategory() {
        // given
        Category category = CategoryFactory.category("Electronics");
        given(categoryRepository.findById(category.getId())).willReturn(Optional.of(category));

        // when
        categoryService.deleteCategory(category.getId());

        // then
        verify(categoryRepository, times(1)).delete(category);
    }

}