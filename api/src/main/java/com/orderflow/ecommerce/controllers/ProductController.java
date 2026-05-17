package com.orderflow.ecommerce.controllers;

import com.orderflow.ecommerce.controllers.docs.ProductControllerDocs;
import com.orderflow.ecommerce.entities.Product;
import com.orderflow.ecommerce.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/products")
public class ProductController implements ProductControllerDocs {

    @Autowired
    private ProductRepository repository;

    @Override
    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok().body(repository.findAll());
    }

    @Override
    @GetMapping(value = "/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        Product obj = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Produto não encontrado"));
        return ResponseEntity.ok().body(obj);
    }

    @Override
    @PostMapping
    public ResponseEntity<Product> insert(@Valid @RequestBody Product obj) {
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
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody Product obj) {
        Product entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Produto não encontrado para atualizar"));

        entity.setName(obj.getName());
        entity.setDescription(obj.getDescription());
        entity.setPrice(obj.getPrice());
        entity.setStockQuantity(obj.getStockQuantity());
        entity.setCategory(obj.getCategory());

        return ResponseEntity.ok().body(repository.save(entity));
    }
}
