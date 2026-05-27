package com.orderflow.ecommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderflow.ecommerce.dtos.CategoryRequest;
import com.orderflow.ecommerce.dtos.CategoryResponse;
import com.orderflow.ecommerce.mappers.CategoryMapper;
import com.orderflow.ecommerce.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private CategoryResponse response;
    private CategoryRequest request;

    @BeforeEach
    void setUp() {
        response = new CategoryResponse(1L, "Eletrônicos");
        request = new CategoryRequest("Eletrônicos");
    }

    @Nested
    class createCategory {
        @Test
        void shouldCreateCategoryWithStatus201() throws Exception {
            when(categoryService.create(any(CategoryRequest.class))).thenReturn(response);

            mockMvc.perform(post("/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Eletrônicos"));
        }

        @Test
        void shouldReturnStatus400WhenCreateWithBlankName() throws Exception {
            CategoryRequest invalidRequest = new CategoryRequest("");

            mockMvc.perform(post("/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnStatus400WhenCreateWithDuplicateName() throws Exception {
            when(categoryService.create(any(CategoryRequest.class)))
                    .thenThrow(new IllegalArgumentException("Já existe uma categoria com o nome: Eletrônicos"));

            mockMvc.perform(post("/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class findAllCategory {
        @Test
        void shouldReturnPagedCategoriesWithStatus200() throws Exception {
            Page<CategoryResponse> page = new PageImpl<>(List.of(response));
            when(categoryService.findAll(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/categories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].name").value("Eletrônicos"));
        }

        @Test
        void shouldReturnFilteredCategoriesByNameWithStatus200() throws Exception {
            Page<CategoryResponse> page = new PageImpl<>(List.of(response));
            when(categoryService.findByName(eq("Eletrônicos"), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/categories")
                            .param("name", "Eletrônicos")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].name").value("Eletrônicos"));
        }
    }

    @Nested
    class findByIdCategory {
        @Test
        void shouldReturnCategoryByIdWithStatus200() throws Exception {
            when(categoryService.findById(1L)).thenReturn(response);

            mockMvc.perform(get("/categories/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Eletrônicos"));
        }

        @Test
        void shouldReturnStatus404WhenCategoryNotFound() throws Exception {
            when(categoryService.findById(99L)).thenThrow(new NoSuchElementException("Categoria não encontrada com ID: 99"));

            mockMvc.perform(get("/categories/99")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class updateCategory {
        @Test
        void shouldUpdateCategoryWithStatus200() throws Exception {
            CategoryResponse updated = new CategoryResponse(1L, "Games");
            when(categoryService.update(eq(1L), any(CategoryRequest.class))).thenReturn(updated);

            mockMvc.perform(put("/categories/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new CategoryRequest("Games"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Games"));
        }

        @Test
        void shouldReturnStatus404WhenUpdateNonExistentCategory() throws Exception {
            when(categoryService.update(eq(99L), any(CategoryRequest.class)))
                    .thenThrow(new NoSuchElementException("Categoria não encontrada com ID: 99"));

            mockMvc.perform(put("/categories/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class deleteCategory {
        @Test
        void shouldDeleteCategoryWithStatus204() throws Exception {
            doNothing().when(categoryService).delete(1L);

            mockMvc.perform(delete("/categories/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturnStatus404WhenDeleteNonExistentCategory() throws Exception {
            doThrow(new NoSuchElementException("Categoria não encontrada com ID: 99"))
                    .when(categoryService).delete(99L);

            mockMvc.perform(delete("/categories/99"))
                    .andExpect(status().isNotFound());
        }
    }
}
