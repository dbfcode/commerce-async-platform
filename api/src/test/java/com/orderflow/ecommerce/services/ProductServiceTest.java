package com.orderflow.ecommerce.services;

import com.orderflow.ecommerce.dtos.CategoryResponse;
import com.orderflow.ecommerce.dtos.ProductFilter;
import com.orderflow.ecommerce.dtos.ProductRequest;
import com.orderflow.ecommerce.dtos.ProductResponse;
import com.orderflow.ecommerce.entities.Category;
import com.orderflow.ecommerce.entities.Product;
import com.orderflow.ecommerce.mappers.ProductMapper;
import com.orderflow.ecommerce.repositories.CategoryRepository;
import com.orderflow.ecommerce.repositories.ProductRepository;
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
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;
    private ProductRequest productRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Eletrônicos");
        product = new Product(1L, "Notebook Dell", "Notebook i7", new BigDecimal("4999.99"), 10, category);
        productRequest = new ProductRequest("Notebook Dell", "Notebook i7", new BigDecimal("4999.99"), 10, 1L);
        productResponse = new ProductResponse(1L, "Notebook Dell", "Notebook i7", new BigDecimal("4999.99"), 10, new CategoryResponse(1L, "Eletrônicos"));
    }

    @Nested
    class CreateProduct {

        @Test
        void shouldCreateProductSuccessfully() {
            when(productRepository.existsByName(productRequest.getName())).thenReturn(false);
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(productMapper.toEntity(productRequest, category)).thenReturn(product);
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toResponse(product)).thenReturn(productResponse);

            ProductResponse result = productService.create(productRequest);

            assertNotNull(result);
            assertEquals("Notebook Dell", result.name());
            verify(productRepository).save(product);
        }

        @Test
        void shouldThrowWhenCreatingProductWithDuplicateName() {
            when(productRepository.existsByName(productRequest.getName())).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () -> productService.create(productRequest));

            verify(productRepository, never()).save(any());
        }

        @Test
        void shouldThrowWhenCreatingProductWithInvalidCategory() {
            when(productRepository.existsByName(productRequest.getName())).thenReturn(false);
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> productService.create(productRequest));

            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    class FindByIdProduct {

        @Test
        void shouldReturnProductWhenFoundById() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productMapper.toResponse(product)).thenReturn(productResponse);

            ProductResponse result = productService.findById(1L);

            assertNotNull(result);
            assertEquals(1L, result.id());
        }

        @Test
        void shouldThrowWhenProductNotFoundById() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> productService.findById(99L));
        }
    }

    @Nested
    class findAllProduct {

        @Test
        void shouldReturnPagedProducts() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> page = new PageImpl<>(List.of(product));
            ProductFilter filter = new ProductFilter(null, null, null, null, null);

            when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
            when(productMapper.toResponse(product)).thenReturn(productResponse);

            Page<ProductResponse> result = productService.findAll(filter, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        void shouldReturnFilteredProductsByName() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> page = new PageImpl<>(List.of(product));
            ProductFilter filter = new ProductFilter("Notebook", null, null, null, null);

            when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
            when(productMapper.toResponse(product)).thenReturn(productResponse);

            Page<ProductResponse> result = productService.findAll(filter, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("Notebook Dell", result.getContent().getFirst().name());
        }

        @Test
        void shouldReturnFilteredProductsByPriceRange() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> page = new PageImpl<>(List.of(product));
            ProductFilter filter = new ProductFilter(null, null, new BigDecimal("1000"), new BigDecimal("6000"), null);

            when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
            when(productMapper.toResponse(product)).thenReturn(productResponse);

            Page<ProductResponse> result = productService.findAll(filter, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        void shouldReturnOnlyInStockProducts() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> page = new PageImpl<>(List.of(product));
            ProductFilter filter = new ProductFilter(null, null, null, null, true);

            when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
            when(productMapper.toResponse(product)).thenReturn(productResponse);

            Page<ProductResponse> result = productService.findAll(filter, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    class UpdateProduct {

        @Test
        void shouldUpdateProductSuccessfully() {
            ProductRequest updateRequest = new ProductRequest("Notebook Dell Pro", "Notebook i9", new BigDecimal("7999.99"), 5, 1L);
            Product updated = new Product(1L, "Notebook Dell Pro", "Notebook i9", new BigDecimal("7999.99"), 5, category);
            ProductResponse updatedResponse = new ProductResponse(1L, "Notebook Dell Pro", "Notebook i9", new BigDecimal("7999.99"), 5, new CategoryResponse(1L, "Eletrônicos"));

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(productRepository.existsByName("Notebook Dell Pro")).thenReturn(false);
            when(productRepository.save(product)).thenReturn(updated);
            when(productMapper.toResponse(updated)).thenReturn(updatedResponse);

            ProductResponse result = productService.update(updateRequest, 1L);

            assertEquals("Notebook Dell Pro", result.name());
            verify(productRepository).save(product);
        }

        @Test
        void shouldThrowWhenUpdatingWithDuplicateName() {
            ProductRequest updateRequest = new ProductRequest("Outro Notebook", "desc", new BigDecimal("999"), 1, 1L);

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(productRepository.existsByName("Outro Notebook")).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () -> productService.update(updateRequest, 1L));

            verify(productRepository, never()).save(any());
        }

        @Test
        void shouldThrowWhenUpdatingNonExistentProduct() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> productService.update(productRequest, 99L));
        }
    }

    @Nested
    class DeleteProduct {

        @Test
        void shouldDeleteProductSuccessfully() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            productService.delete(1L);

            verify(productRepository).delete(product);
        }

        @Test
        void shouldThrowWhenDeletingNonExistentProduct() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> productService.delete(99L));

            verify(productRepository, never()).delete((Product) any());
        }
    }
}
