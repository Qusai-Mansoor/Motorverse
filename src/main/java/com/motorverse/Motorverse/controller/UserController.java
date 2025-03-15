package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.User;
import com.motorverse.Motorverse.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        // For simplicity, no password hashing yet (add in production)
        User newUser = new User(
            user.getEmail(),
            user.getPassword(),
            user.getFirstName(),
            user.getLastName(),
            user.getDateOfBirth(),
            user.getPhoneNumber()
        );

        userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully");
    }
}