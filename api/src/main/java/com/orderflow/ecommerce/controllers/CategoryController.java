package com.orderflow.ecommerce.controllers;

import com.orderflow.ecommerce.controllers.docs.CategoryControllerDocs;
import com.orderflow.ecommerce.entities.Category;
import com.orderflow.ecommerce.repositories.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController implements CategoryControllerDocs {

    @Autowired
    private CategoryRepository repository;

    @Override
    @GetMapping
    public ResponseEntity<List<Category>> findAll() {
        return ResponseEntity.ok().body(repository.findAll());
    }

    @Override
    @GetMapping(value = "/{id}")
    public ResponseEntity<Category> findById(@PathVariable Long id) {
        Category obj = repository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Categoria não encontrada com o ID: " + id));
        return ResponseEntity.ok().body(obj);
    }

    @Override
    @PostMapping
    public ResponseEntity<Category> insert(@Valid @RequestBody Category obj) {
        return ResponseEntity.ok().body(repository.save(obj));
    }

    @Override
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PutMapping(value = "/{id}")
    public ResponseEntity<Category> update(
        @PathVariable Long id,
        @Valid @RequestBody Category obj
    ) {
        Category entity = repository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Categoria não encontrada para atualizar"));
        entity.setName(obj.getName());
        return ResponseEntity.ok().body(repository.save(entity));
    }
}
