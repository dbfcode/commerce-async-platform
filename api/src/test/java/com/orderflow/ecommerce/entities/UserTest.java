package com.orderflow.ecommerce.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class UserTest {

    @Test
    void shouldInstantiateWithAllArgsConstructor() {
        // Arrange & Act
        User user = new User(1L, "Alice", "alice@example.com", "password123",
                "12345678901", "IE123456", "11987654321",
                LocalDate.of(1990, 5, 15), true, "google-id-123",
                "Rua A", "Apt 101", "100", "Centro", "São Paulo",
                "Brazil", "SP", "01000-000");

        // Assert
        assertEquals(1L, user.getId());
        assertEquals("Alice", user.getName());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("12345678901", user.getTaxId());
        assertEquals("IE123456", user.getStateRegistration());
        assertEquals("11987654321", user.getPhone());
        assertEquals(LocalDate.of(1990, 5, 15), user.getBirthDate());
        assertTrue(user.getTaxpayer());
        assertEquals("google-id-123", user.getGoogleId());
        assertEquals("Rua A", user.getStreet());
        assertEquals("Apt 101", user.getComplement());
        assertEquals("100", user.getNumber());
        assertEquals("Centro", user.getNeighborhood());
        assertEquals("São Paulo", user.getCity());
        assertEquals("Brazil", user.getCountry());
        assertEquals("SP", user.getState());
        assertEquals("01000-000", user.getZipCode());
    }

    @Test
    void shouldHaveNullsWhenUsingNoArgsConstructor() {
        // Arrange & Act
        User user = new User();

        // Assert
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
    }

    @Test
    void shouldUseSettersAndGetters() {
        // Arrange
        User user = new User();

        // Act
        user.setId(2L);
        user.setName("Bob");
        user.setEmail("bob@example.com");
        user.setPassword("secret");
        user.setTaxId("98765432101");
        user.setPhone("119876543");
        user.setBirthDate(LocalDate.of(1985, 3, 20));
        user.setTaxpayer(false);
        user.setStreet("Rua B");
        user.setState("RJ");
        user.setZipCode("20000-000");

        // Assert
        assertEquals(2L, user.getId());
        assertEquals("Bob", user.getName());
        assertEquals("bob@example.com", user.getEmail());
        assertEquals("secret", user.getPassword());
        assertEquals("98765432101", user.getTaxId());
        assertEquals("119876543", user.getPhone());
        assertEquals(LocalDate.of(1985, 3, 20), user.getBirthDate());
        assertFalse(user.getTaxpayer());
        assertEquals("Rua B", user.getStreet());
        assertEquals("RJ", user.getState());
        assertEquals("20000-000", user.getZipCode());
    }

    @Test
    void equalsAndHashCodeShouldBeBasedOnlyOnId() {
        User a = new User(1L, "A", "a@x", "p", "tax", null, null, null, null, null, null, null, null, null, null, null, null, null);
        User b = new User(1L, "B", "b@x", "q", "tax2", null, null, null, null, null, null, null, null, null, null, null, null, null);
        User c = new User(2L, "A", "a@x", "p", "tax", null, null, null, null, null, null, null, null, null, null, null, null, null);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        assertNotEquals(a, c);
        assertNotEquals(a.hashCode(), c.hashCode());
    }

}
