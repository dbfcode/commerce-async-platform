package com.orderflow.ecommerce.services;

import com.orderflow.ecommerce.dtos.UserDto;
import com.orderflow.ecommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        return new UserDto(repository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found")));
    }

    @Transactional(readOnly = true)
    public UserDto findByEmail(String email) {
        return new UserDto(repository.findByEmailIgnoreCase(email).orElseThrow(() -> new NoSuchElementException("User not found")));
    }

    @Transactional(readOnly = true)
    public Page<UserDto> findAllPaged(Pageable pageable) {
        return repository.findAll(pageable).map(UserDto::new);
    }
}
