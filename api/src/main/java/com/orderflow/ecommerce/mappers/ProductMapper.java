package com.orderflow.ecommerce.mappers;

import com.orderflow.ecommerce.dtos.ProductRequest;
import com.orderflow.ecommerce.dtos.ProductResponse;
import com.orderflow.ecommerce.entities.Category;
import com.orderflow.ecommerce.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ProductMapper {

    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductRequest request, Category category);
}
