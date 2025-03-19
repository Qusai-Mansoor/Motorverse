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
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest credentials) {
        User user = userRepository.findByEmail(credentials.getEmail());
        if (user == null || !user.getPassword().equals(credentials.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        // Mock token (replace with JWT in production)
        String token = "mock-jwt-" + System.currentTimeMillis();

        // Return token and userId in response
        LoginResponse response = new LoginResponse(token, user.getId(), user.getFirstName(), user.getEmail(), user.getPhoneNumber());
        return ResponseEntity.ok(response);
    }
}

class LoginRequest {
    private String email;
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class LoginResponse {
    private String token;
    private int userId;
    private String firstName;
    private String email;
    private String phoneNumber;

    public LoginResponse(String token, int userId, String firstName, String email, String phoneNumber) {
        this.token = token;
        this.userId = userId;
        this.firstName = firstName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters
    public String getToken() { return token; }
    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
}