package com.orderflow.ecommerce.dtos;

import com.orderflow.ecommerce.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserDto(
        Long id,
        @NotBlank(message = "Campo requerido")
        String name,
        @NotBlank(message = "Campo requerido")
        @Email(message = "Email inválido")
        String email,
        @NotBlank(message = "Campo requerido")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
                message = "A senha deve conter pelo menos 8 caracteres, incluindo letras maiúsculas, minúsculas, números e caracteres especiais"
        )
        String password,
        String taxId,
        String stateRegistration,
        String phone,
        LocalDate birthDate,
        Boolean taxpayer,
        String googleId,
        @Size(max = 40, message = "Máximo 40 caracteres")
        String street,
        @Size(max = 40, message = "Máximo 40 caracteres")
        String complement,
        @Size(max = 10, message = "Máximo 10 caracteres")
        String number,
        @Size(max = 40, message = "Máximo 40 caracteres")
        String neighborhood,
        @Size(max = 40, message = "Máximo 40 caracteres")
        String city,
        @Size(max = 40, message = "Máximo 40 caracteres")
        String country,
        @Size(max = 2, message = "Máximo 2 caracteres")
        String state,
        @Size(max = 10, message = "Máximo 10 caracteres")
        String zipCode
) {
    public UserDto(User entity) {
        this(entity.getId(), entity.getName(), entity.getEmail(), entity.getPassword(), entity.getTaxId(), entity.getStateRegistration(), entity.getPhone(), entity.getBirthDate(), entity.getTaxpayer(), entity.getGoogleId(), entity.getStreet(), entity.getComplement(), entity.getNumber(), entity.getNeighborhood(), entity.getCity(), entity.getCountry(), entity.getState(), entity.getZipCode());
    }
}


