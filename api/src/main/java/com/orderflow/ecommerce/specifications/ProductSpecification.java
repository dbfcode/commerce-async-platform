package com.orderflow.ecommerce.specifications;

import com.orderflow.ecommerce.dtos.ProductFilter;
import com.orderflow.ecommerce.entities.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> withFilters(ProductFilter filter) {
        return Specification
                .where(hasName(filter.name()))
                .and(hasCategory(filter.categoryName()))
                .and(hasPriceBetween(filter.minPrice(), filter.maxPrice()));
    }

    private static Specification<Product> hasName(String name) {
        return (root, query, cb) -> name == null || name.isBlank()
                ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Product> hasCategory(String categoryName) {
        return (root, query, cb) -> categoryName == null || categoryName.isBlank()
            ? null
            : cb.like(
                cb.lower(root.get("category").get("name")),
                "%" + categoryName.toLowerCase() + "%"
            );
    }

    private static Specification<Product> hasPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if(minPrice != null && maxPrice!= null) {
                return cb.between(root.get("price"), minPrice, maxPrice);
            }
            if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            }
            if (maxPrice != null) {
                return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
            return null;
        };
    }

    private static Specification<Product> isInStock (Boolean inStock) {
        return (root, query, cb) -> inStock == null || !inStock
                ? null : cb.greaterThan(root.get("stockQuantity"), 0);
    }
}
