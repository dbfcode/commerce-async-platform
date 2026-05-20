package com.orderflow.ecommerce.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "O nome da categoria é obrigatório")
    private String name;
}
