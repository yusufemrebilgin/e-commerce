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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @ParameterizedTest
    @CsvSource({
            "0, 5, 3",
            "0, 2, 2",
            "1, 2, 1",
            "1, 3, 0"
    })
    void givenPaginationParameters_whenSuccess_returnPaginatedResponse(int page, int size, int expectedSize) {
        // given
        Pageable pageable = PageableFactory.getPageable(page, size, "name:asc");

        Category category1 = new Category(1L, "Clothing");
        Category category2 = new Category(2L, "Computers");
        Category category3 = new Category(3L, "Electronics");

        List<Category> categories = List.of(category1, category2, category3);

        // from: page * size
        // to  : (page + 1) * size or categories.size()
        Page<Category> categoryPage = new PageImpl<>(categories.subList(page * size, Math.min((page + 1) * size, categories.size())));

        List<CategoryDto> categoryDtoList = List.of(
                new CategoryDto(category1.getId(), category1.getName()),
                new CategoryDto(category2.getId(), category2.getName()),
                new CategoryDto(category3.getId(), category3.getName())
        );
        List<CategoryDto> expectedDtoList = categoryDtoList.subList(0, expectedSize);

        PaginatedResponse<CategoryDto> expected = new PaginatedResponse<>(
                expectedDtoList,
                page,
                size,
                categoryPage.getTotalPages(),
                categoryPage.getNumberOfElements(),
                categoryPage.isLast()
        );

        given(categoryRepository.findAll(pageable)).willReturn(categoryPage);
        given(paginationMapper.toPaginatedResponse(categoryPage, categoryMapper)).willReturn(expected);

        // when
        PaginatedResponse<CategoryDto> actual = categoryService.getAllCategories(page, size, "name:asc");

        // then
        then(actual).isNotNull();
        then(actual.content()).hasSize(expectedSize);
        for (int i = 0; i < expectedSize; i++) {
            then(actual.content().get(i).categoryName()).isEqualTo(expectedDtoList.get(i).categoryName());
        }

        verify(categoryRepository, times(1)).findAll(pageable);
        verify(paginationMapper, times(1)).toPaginatedResponse(categoryPage, categoryMapper);
    }

    @Test
    void givenCategoryId_whenCategoryFound_returnCategory() {
        // given
        Long categoryId = 1L;
        Category expected = new Category(categoryId, "Electronics");
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(expected));

        // when
        Category actual = categoryService.getCategoryById(categoryId);

        // get
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(actual.getId()).isEqualTo(categoryId);
        then(actual.getName()).isEqualTo(expected.getName());
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
        CategoryDto expectedDto = new CategoryDto(0L, request.categoryName());

        given(categoryRepository.save(category)).willReturn(category);
        given(categoryMapper.mapToDto(category)).willReturn(expectedDto);

        // when
        CategoryDto actualDto = categoryService.createCategory(request);

        // then
        then(actualDto).isNotNull();
        then(actualDto).isEqualTo(expectedDto);
        then(actualDto.categoryName()).isEqualTo(expectedDto.categoryName());
    }

    @Test
    void givenUpdateCategoryRequest_whenUpdated_returnUpdatedCategoryDto() {
        //given
        Long categoryId = 1L;
        UpdateCategoryRequest request = new UpdateCategoryRequest("Electronics");

        Category existingCategory = new Category(categoryId, "Computers");
        Category updatedCategory = new Category(categoryId, "Electronics");
        CategoryDto expectedDto = new CategoryDto(categoryId, "Electronics");

        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(existingCategory));
        given(categoryRepository.save(any(Category.class))).willReturn(updatedCategory);
        given(categoryMapper.mapToDto(existingCategory)).willReturn(expectedDto);

        // when
        CategoryDto actualDto = categoryService.updateCategory(categoryId, request);

        // then
        then(actualDto).isNotNull();
        then(actualDto.categoryName()).isEqualTo(expectedDto.categoryName());
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