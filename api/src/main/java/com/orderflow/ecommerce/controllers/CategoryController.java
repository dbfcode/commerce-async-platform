package com.orderflow.ecommerce.controllers;

import com.orderflow.ecommerce.controllers.docs.CategoryControllerDocs;
import com.orderflow.ecommerce.dtos.CategoryRequest;
import com.orderflow.ecommerce.dtos.CategoryResponse;
import com.orderflow.ecommerce.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController implements CategoryControllerDocs {

    @Autowired
    private CategoryService categoryService;

    @Override
    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> findAll(
            @RequestParam(required = false) String name,
            Pageable pageable
    ) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(categoryService.findByName(name, pageable));
        }
        return ResponseEntity.ok(categoryService.findAll(pageable));
    }

    @Override
    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @Override
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PutMapping(value = "/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @Override
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
