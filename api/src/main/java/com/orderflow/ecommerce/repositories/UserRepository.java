package com.orderflow.ecommerce.repositories;

import com.orderflow.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByTaxId(String taxId);
    boolean existsByTaxIdAndIdNot(String taxId, Long id);
}
