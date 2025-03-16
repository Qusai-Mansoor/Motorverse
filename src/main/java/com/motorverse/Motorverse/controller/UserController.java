package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.User;
import com.motorverse.Motorverse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }
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

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody User credentials) {
        User user = userRepository.findByEmail(credentials.getEmail());
        if (user == null) {
            return ResponseEntity.badRequest().body(null);
        }
        if (!user.getPassword().equals(credentials.getPassword())) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(user); // Returns full User object
    }
}