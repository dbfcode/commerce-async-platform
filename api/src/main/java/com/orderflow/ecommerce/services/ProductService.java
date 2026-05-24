package com.orderflow.ecommerce.services;

import com.orderflow.ecommerce.dtos.ProductRequest;
import com.orderflow.ecommerce.dtos.ProductResponse;
import com.orderflow.ecommerce.entities.Category;
import com.orderflow.ecommerce.entities.Product;
import com.orderflow.ecommerce.mappers.ProductMapper;
import com.orderflow.ecommerce.repositories.CategoryRepository;
import com.orderflow.ecommerce.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductMapper productMapper;

    public ProductResponse create(ProductRequest request) {
        validateProduct(request, null);

        Category category = findCategoryById(request.getCategoryId());
        Product entity = productMapper.toEntity(request, category);
        Product saved = productRepository.save(entity);
        log.info("Product created with ID: {}", saved.getId());
        return productMapper.toResponse(saved);
    }

    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    public ProductResponse findById(Long id) {
        Product entity = findEntityById(id);
        return productMapper.toResponse(entity);
    }

    public ProductResponse update(ProductRequest request, Long id) {
        Product existing = findEntityById(id);
        Category category = findCategoryById(request.getCategoryId());
        validateProduct(request, existing);
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setStockQuantity(request.getStockQuantity());
        existing.setCategory(category);
        Product saved = productRepository.save(existing);
        log.info("Product updated with ID: {}", saved.getId());
        return productMapper.toResponse(saved);
    }

    public void delete(Long id) {
        Product entity = findEntityById(id);
        productRepository.delete(entity);
        log.info("Product deleted with ID: {}", id);
    }

    private Product findEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Produto não encontrado com ID: " + id));
    }

    private void validateProduct(ProductRequest request, Product existing) {
        boolean nameChanged = existing == null || !existing.getName().equals(request.getName());
        if (nameChanged && productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Já existe um produto com o nome: " + request.getName());
        }
    }

    private Category findCategoryById(Long categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("Categoria não encotrada com ID: " + categoryId));
    }
}
