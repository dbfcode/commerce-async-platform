package com.orderflow.ecommerce.dtos;

import java.math.BigDecimal;

public record ProductFilter(
        String name,
        String categoryName,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Boolean inStock
) {}
