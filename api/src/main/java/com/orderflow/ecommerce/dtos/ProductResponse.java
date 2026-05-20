package com.orderflow.ecommerce.dtos;

import java.math.BigDecimal;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stockQuantity,
    CategoryResponse category
) {}
