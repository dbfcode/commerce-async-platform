package com.orderflow.ecommerce.services;

import com.orderflow.ecommerce.dtos.UserDto;
import com.orderflow.ecommerce.entities.User;
import com.orderflow.ecommerce.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @Transactional
    public UserDto insert(UserDto dto) {
        return new UserDto(saveEntity(null, dto));
    }

    @Transactional
    public UserDto update(Long id, UserDto dto) {
        try {
            return new UserDto(saveEntity(id, dto));
        }
        catch (EntityNotFoundException e) {
            throw new NoSuchElementException("Id not found " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Integrity violation");
        }
    }

    private User saveEntity(Long id, UserDto dto) {
        User entity = new User();

        if(id != null) entity = repository.getReferenceById(id);

        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setPassword(dto.password());
        entity.setTaxId(dto.taxId());
        entity.setStateRegistration(dto.stateRegistration());
        entity.setPhone(dto.phone());
        entity.setBirthDate(dto.birthDate());
        entity.setTaxpayer(dto.taxpayer());
        entity.setGoogleId(dto.googleId());
        entity.setStreet(dto.street());
        entity.setComplement(dto.complement());
        entity.setNumber(dto.number());
        entity.setNeighborhood(dto.neighborhood());
        entity.setCity(dto.city());
        entity.setCountry(dto.country());
        entity.setState(dto.state());
        entity.setZipCode(dto.zipCode());

        return repository.save(entity);
    }
}
