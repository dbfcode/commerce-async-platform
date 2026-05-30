package com.orderflow.ecommerce.auxiliar;

import com.orderflow.ecommerce.dtos.UserDto;
import com.orderflow.ecommerce.entities.User;

import java.time.LocalDate;

public class Factory {
    public static User createUser(){
        User user = new User();
        user.setId(1L);
        user.setName("Bob");
        user.setEmail("bob@gmail.com");
        user.setPassword("Shh..1secret");
        user.setTaxId("98765432101");
        user.setPhone("1198765432");
        user.setBirthDate(LocalDate.of(1985, 3, 20));
        user.setTaxpayer(false);
        user.setStreet("Rua B");
        user.setState("RJ");
        user.setZipCode("20000-000");
        return user;
    }

    public static UserDto createUserDto(){
        User user = createUser();
        return new UserDto(user);
    }

}
