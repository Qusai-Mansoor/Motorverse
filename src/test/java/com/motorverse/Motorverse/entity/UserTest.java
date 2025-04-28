package com.motorverse.Motorverse.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testUserGettersAndSetters() {
        User user = new User();
        user.setId(1);
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setEmail("alice@example.com");
        user.setPassword("secret");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user.setPhoneNumber("1234567890");
        user.setRole(User.Role.USER);
        user.setStatus(User.Status.ACTIVE);
        user.setPicture("avatar.jpg");
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now); // Test createdAt setter

        assertEquals(1, user.getId());
        assertEquals("Alice", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("secret", user.getPassword());
        assertEquals(LocalDate.of(1990, 1, 1), user.getDateOfBirth());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals(User.Role.USER, user.getRole());
        assertEquals(User.Status.ACTIVE, user.getStatus());
        assertEquals("avatar.jpg", user.getPicture());
        assertEquals(now, user.getCreatedAt()); // Test createdAt getter
    }

    @Test
    void testUserDefaultConstructor() {
        User user = new User();
        assertNotNull(user);
        assertNull(user.getEmail()); // Fields should be null or default initially
        assertEquals(0, user.getId());
        assertEquals(User.Role.USER, user.getRole()); // Default role
        assertEquals(User.Status.ACTIVE, user.getStatus()); // Default status
        assertEquals("default-avatar.jpg", user.getPicture()); // Default picture
        assertNotNull(user.getCreatedAt()); // Should be set automatically
    }

    @Test
    void testUserConstructorWithRequiredFields() {
        LocalDate dob = LocalDate.of(1995, 5, 15);
        User user = new User("bob@example.com", "pass123", "Bob", "Johnson", dob, "0987654321");

        assertEquals("bob@example.com", user.getEmail());
        assertEquals("pass123", user.getPassword());
        assertEquals("Bob", user.getFirstName());
        assertEquals("Johnson", user.getLastName());
        assertEquals(dob, user.getDateOfBirth());
        assertEquals("0987654321", user.getPhoneNumber());
        assertEquals(User.Role.USER, user.getRole()); // Default role
        assertEquals(User.Status.ACTIVE, user.getStatus()); // Default status
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void testUserConstructorWithRole() {
        LocalDate dob = LocalDate.of(1985, 8, 25);
        User user = new User("admin@example.com", "adminpass", "Admin", "User", dob, "1122334455", User.Role.ADMIN);

        assertEquals("admin@example.com", user.getEmail());
        assertEquals("adminpass", user.getPassword());
        assertEquals("Admin", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals(dob, user.getDateOfBirth());
        assertEquals("1122334455", user.getPhoneNumber());
        assertEquals(User.Role.ADMIN, user.getRole()); // Specified role
        assertEquals(User.Status.ACTIVE, user.getStatus()); // Default status
        assertNotNull(user.getCreatedAt());
    }


    @Test
    void testUserRoleEnum() {
        assertEquals("USER", User.Role.USER.name());
        assertEquals("ADMIN", User.Role.ADMIN.name());
        assertEquals(User.Role.USER, User.Role.valueOf("USER"));
        assertEquals(User.Role.ADMIN, User.Role.valueOf("ADMIN"));
    }

    @Test
    void testUserStatusEnum() {
        assertEquals("ACTIVE", User.Status.ACTIVE.name());
        assertEquals("INACTIVE", User.Status.INACTIVE.name());
        assertEquals("SUSPENDED", User.Status.SUSPENDED.name()); // Updated enum value
        assertEquals(User.Status.ACTIVE, User.Status.valueOf("ACTIVE"));
        assertEquals(User.Status.INACTIVE, User.Status.valueOf("INACTIVE"));
        assertEquals(User.Status.SUSPENDED, User.Status.valueOf("SUSPENDED")); // Updated enum value
    }
}
