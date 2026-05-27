package com.orderflow.ecommerce.services;

import com.orderflow.ecommerce.dtos.CategoryRequest;
import com.orderflow.ecommerce.dtos.CategoryResponse;
import com.orderflow.ecommerce.entities.Category;
import com.orderflow.ecommerce.mappers.CategoryMapper;
import com.orderflow.ecommerce.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryRequest categoryRequest;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Eletrônicos");
        categoryRequest = new CategoryRequest("Eletrônicos");
        categoryResponse = new CategoryResponse(1L, "Eletrônicos");
    }

    @Nested
    class CreateCategory {

        @Test
        void shouldCreateCategorySuccessfully() {
            when(categoryRepository.existsByName(categoryRequest.getName())).thenReturn(false);
            when(categoryMapper.toModel(categoryRequest)).thenReturn(category);
            when(categoryRepository.save(category)).thenReturn(category);
            when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

            CategoryResponse result = categoryService.create(categoryRequest);

            assertNotNull(result);
            assertEquals("Eletrônicos", result.name());
            verify(categoryRepository).save(category);
        }

        @Test
        void shouldThrowWhenCreatingCategoryWithDuplicateName() {
            when(categoryRepository.existsByName(categoryRequest.getName())).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> categoryService.create(categoryRequest));

            assertEquals("Já existe uma categoria com o nome: Eletrônicos", ex.getMessage());
            verify(categoryRepository, never()).save(any());
        }

    }

    @Nested
    class FindByIdCategory {

        @Test
        void shouldReturnCategoryWhenFoundById () {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

            CategoryResponse result = categoryService.findById(1L);

            assertNotNull(result);
            assertEquals(1L, result.id());
        }

        @Test
        void shouldThrowWhenCategoryNotFoundById() {
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> categoryService.findById(99L));
        }
    }

    @Nested
    class FindAllCategory {

        @Test
        void shouldReturnPagedCategories() {
            Pageable pageable = PageRequest.of(0,10);
            Page<Category> page = new PageImpl<>(List.of(category));

            when(categoryRepository.findAll(pageable)).thenReturn(page);
            when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

            Page<CategoryResponse> result = categoryService.findAll(pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        void shouldReturnFilteredCategoriesWhenNameParamProvided() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Category> page = new PageImpl<>(List.of(category));

            when(categoryRepository.findByName("Eletrônicos", pageable)).thenReturn(page);
            when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

            Page<CategoryResponse> result = categoryService.findByName("Eletrônicos", pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("Eletrônicos", result.getContent().getFirst().name());
            verify(categoryRepository).findByName("Eletrônicos", pageable);
            verify(categoryRepository, never()).findAll(pageable);
        }
    }

    @Nested
    class UpdateCategory {

        @Test
        void shouldUpdateCategorySuccessfully() {
            CategoryRequest updateRequest = new CategoryRequest("Games");
            Category updated = new Category(1L, "Games");
            CategoryResponse updatedResponse = new CategoryResponse(1L, "Games");

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.existsByName("Games")).thenReturn(false);
            when(categoryRepository.save(category)).thenReturn(updated);
            when(categoryMapper.toResponse(updated)).thenReturn(updatedResponse);

            CategoryResponse result = categoryService.update(1L, updateRequest);

            assertEquals("Games", result.name());
            verify(categoryRepository).save(category);
        }

        @Test
        void shouldThrowWhenUpdatingWithDuplicateName() {
            CategoryRequest updateRequest = new CategoryRequest("Games");

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.existsByName("Games")).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () -> categoryService.update(1L, updateRequest));
            verify(categoryRepository, never()).save(any());
        }

        @Test
        void shouldThrowWhenUpdatingNonExistentCategory() {
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> categoryService.update(99L, categoryRequest));
        }
    }

    @Nested
    class DeleteCategory {

        @Test
        void shouldDeleteCategorySuccessfully() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            categoryService.delete(1L);

            verify(categoryRepository).delete(category);
        }

        @Test
        void shouldThrowWhenDeletingNonExistentCategory() {
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class,
                    () -> categoryService.delete(99L));

            verify(categoryRepository, never()).delete(any());
        }
    }
}
