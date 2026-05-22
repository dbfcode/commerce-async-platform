package com.orderflow.ecommerce.services;

import com.orderflow.ecommerce.dtos.UserDto;
import com.orderflow.ecommerce.entities.User;
import com.orderflow.ecommerce.exceptions.DuplicateResourceException;
import com.orderflow.ecommerce.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

        if(id != null){ // if updating
            entity = repository.getReferenceById(id);
            if (repository.existsByEmailAndIdNot(dto.email(), id)) {
                throw new DuplicateResourceException("Email já cadastrado para outro usuário!");
            }
            if (repository.existsByTaxIdAndIdNot(dto.taxId(), id)) {
                throw new DuplicateResourceException("CPF/CNPJ já cadastrado para outro usuário!");
            }
        } else {
            if (repository.existsByEmail(dto.email())) {
                throw new DuplicateResourceException("Email já cadastrado!");
            }
            if (repository.existsByTaxId(dto.taxId())) {
                throw new DuplicateResourceException("CPF/CNPJ já cadastrado!");
            }
        }


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
