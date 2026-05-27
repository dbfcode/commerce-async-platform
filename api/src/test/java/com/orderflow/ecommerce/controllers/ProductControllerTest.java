package com.orderflow.ecommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderflow.ecommerce.dtos.CategoryResponse;
import com.orderflow.ecommerce.dtos.ProductFilter;
import com.orderflow.ecommerce.dtos.ProductRequest;
import com.orderflow.ecommerce.dtos.ProductResponse;
import com.orderflow.ecommerce.services.ProductService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductResponse response;
    private ProductRequest request;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        categoryResponse = new CategoryResponse(1L, "Eletrônicos");
        response = new ProductResponse(1L, "Notebook Dell", "Notebook i7", new BigDecimal("4999.99"), 10, categoryResponse);
        request = new ProductRequest("Notebook Dell", "Notebook i7", new BigDecimal("4999.99"), 10, 1L);
    }

    @Nested
    class createProduct {

        @Test
        void shouldCreateProductWithStatus201() throws Exception {
            when(productService.create(any(ProductRequest.class))).thenReturn(response);

            mockMvc.perform(post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Notebook Dell"));
        }

        @Test
        void shouldReturnStatus400WhenCreateWithBlankName() throws Exception {
            ProductRequest invalidRequest = new ProductRequest("", "desc", new BigDecimal("999"), 1, 1L);

            mockMvc.perform(post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnStatus400WhenCreateWithDuplicateName() throws Exception {
            when(productService.create(any(ProductRequest.class)))
                    .thenThrow(new IllegalArgumentException("Já existe um produto com o nome: Notebook Dell"));

            mockMvc.perform(post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnStatus400WhenCreateWithNullPrice() throws Exception {
            ProductRequest invalidRequest = new ProductRequest("Notebook Dell", "desc", null, 1, 1L);

            mockMvc.perform(post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class findAllProduct {

        @Test
        void shouldReturnPagedProductsWithStatus200() throws Exception {
            Page<ProductResponse> page = new PageImpl<>(List.of(response));
            when(productService.findAll(any(ProductFilter.class), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/products")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].name").value("Notebook Dell"));
        }

        @Test
        void shouldReturnFilteredProductsByNameWithStatus200() throws Exception {
            Page<ProductResponse> page = new PageImpl<>(List.of(response));
            when(productService.findAll(any(ProductFilter.class), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/products")
                            .param("name", "Notebook")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].name").value("Notebook Dell"));
        }

        @Test
        void shouldReturnFilteredProductsByPriceRangeWithStatus200() throws Exception {
            Page<ProductResponse> page = new PageImpl<>(List.of(response));
            when(productService.findAll(any(ProductFilter.class), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/products")
                            .param("minPrice", "1000")
                            .param("maxPrice", "6000")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].price").value(4999.99));
        }

        @Test
        void shouldReturnFilteredProductsInStockWithStatus200() throws Exception {
            Page<ProductResponse> page = new PageImpl<>(List.of(response));
            when(productService.findAll(any(ProductFilter.class), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/products")
                            .param("inStock", "true")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].stockQuantity").value(10));
        }
    }

    @Nested
    class findByIdProduct {

        @Test
        void shouldReturnProductByIdWithStatus200() throws Exception {
            when(productService.findById(1L)).thenReturn(response);

            mockMvc.perform(get("/products/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Notebook Dell"));
        }

        @Test
        void shouldReturnStatus404WhenProductNotFound() throws Exception {
            when(productService.findById(99L))
                    .thenThrow(new NoSuchElementException("Produto não encontrado com ID: 99"));

            mockMvc.perform(get("/products/99")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class updateProduct {

        @Test
        void shouldUpdateProductWithStatus200() throws Exception {
            ProductResponse updated = new ProductResponse(1L, "Notebook Dell Pro", "Notebook i9", new BigDecimal("7999.99"), 5, categoryResponse);
            when(productService.update(any(ProductRequest.class), eq(1L))).thenReturn(updated);

            mockMvc.perform(put("/products/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Notebook Dell Pro"));
        }

        @Test
        void shouldReturnStatus404WhenUpdateNonExistentProduct() throws Exception {
            when(productService.update(any(ProductRequest.class), eq(99L)))
                    .thenThrow(new NoSuchElementException("Produto não encontrado com ID: 99"));

            mockMvc.perform(put("/products/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class deleteProduct {

        @Test
        void shouldDeleteProductWithStatus204() throws Exception {
            doNothing().when(productService).delete(1L);

            mockMvc.perform(delete("/products/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturnStatus404WhenDeleteNonExistentProduct() throws Exception {
            doThrow(new NoSuchElementException("Produto não encontrado com ID: 99"))
                    .when(productService).delete(99L);

            mockMvc.perform(delete("/products/99"))
                    .andExpect(status().isNotFound());
        }
    }
}
