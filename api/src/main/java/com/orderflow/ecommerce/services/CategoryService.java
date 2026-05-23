package com.orderflow.ecommerce.services;

import com.orderflow.ecommerce.dtos.CategoryRequest;
import com.orderflow.ecommerce.dtos.CategoryResponse;
import com.orderflow.ecommerce.entities.Category;
import com.orderflow.ecommerce.mappers.CategoryMapper;
import com.orderflow.ecommerce.repositories.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class CategoryService {

    private final static Logger log = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    public CategoryResponse create(CategoryRequest request) {
        validateCategory(request, null);

        // TO-DO: aplicar links HATEOAS
        Category entity = categoryMapper.toModel(request);
        Category saved = categoryRepository.save(entity);
        log.info("Category created with ID: {} and name: {}", saved.getId(), saved.getName());
        return categoryMapper.toResponse(saved);
    }

    // TO-DO: adicionar paginação com suporte para links HATEAOS
    public Page<CategoryResponse> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toResponse);
    }

    // TO-DO: adicionar paginação com suporte para links HATEAOS
    public Page<CategoryResponse> findByName(String name, Pageable pageable) {
        return categoryRepository.findByName(name, pageable)
                .map(categoryMapper::toResponse);
    }

    public CategoryResponse findById(Long id) {
        Category entity = findEntityById(id);
        return categoryMapper.toResponse(entity);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category existing = findEntityById(id);
        validateCategory(request, existing);
        existing.setName(request.getName());
        Category saved = categoryRepository.save(existing);
        log.info("Category updated with ID: {} and name: {}", saved.getId(), saved.getName());
        return categoryMapper.toResponse(saved);
    }

    public void delete(Long id) {
        Category entity = findEntityById(id);
        categoryRepository.delete(entity);
        log.info("Category deleted with ID: {}",id);
    }

    private Category findEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoria não encontrada com ID: " + id));
    }

    private void validateCategory(CategoryRequest request, Category existing) {
        boolean nameChanged = existing == null || !existing.getName().equals(request.getName());
        if (nameChanged && categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Já existe uma categoria com o nome: " + request.getName());
        }
    }
}
