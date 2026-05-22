package com.orderflow.ecommerce.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tb_user")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;


    // Customer information for Invoices
    /**
     * CPF or CNPJ
     */
    @Column(name = "tax_id", nullable = false, unique = true, length = 20)
    private String taxId;

    /**
     * State registration (IE)
     */
    @Column(length = 30)
    private String stateRegistration;

    private String phone;
    private LocalDate birthDate;

    /**
     * Used in Invoices (NF-e)
     */
    private Boolean taxpayer;

    /**
     * Google API
     */
    @Column(unique = true)
    private String googleId;

    /**
     * private Address address;
     */
    @Column(length = 40)
    private String street;
    @Column(length = 40)
    private String complement;
    @Column(length = 10)
    private String number;
    @Column(length = 40)
    private String neighborhood;
    @Column(length = 40)
    private String city;
    @Column(length = 40)
    private String country;
    @Column(length = 2)
    private String state;
    @Column(length = 10)
    private String zipCode;

}
