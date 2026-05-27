package com.orderflow.ecommerce.mappers;

import com.orderflow.ecommerce.dtos.CategoryRequest;
import com.orderflow.ecommerce.dtos.CategoryResponse;
import com.orderflow.ecommerce.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    @Mapping(target = "id", ignore = true)
    Category toModel(CategoryRequest request);
}
