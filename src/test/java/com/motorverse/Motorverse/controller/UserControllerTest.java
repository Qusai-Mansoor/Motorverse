package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.User;
import com.motorverse.Motorverse.repository.UserRepository;
import com.motorverse.Motorverse.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Import the actual LoginRequest class
class LoginRequest {
    private String email;
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private BCryptPasswordEncoder encoder;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("password");
        testUser.setRole(User.Role.USER);
        testUser.setStatus(User.Status.ACTIVE);
        testUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testUser.setPhoneNumber("1234567890");
        testUser.setPicture("avatar.jpg");
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        ResponseEntity<List<User>> response = userController.getAllUsers();
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody()); // Check for null before accessing
        assertEquals(1, response.getBody().size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_Found() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        ResponseEntity<User> response = userController.getUserById(1);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody()); // Check for null before accessing
        assertEquals(testUser, response.getBody());
        verify(userRepository).findById(1);
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(2)).thenReturn(Optional.empty());
        ResponseEntity<User> response = userController.getUserById(2);
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(userRepository).findById(2);
    }

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        User userToCreate = new User();
        userToCreate.setFirstName("Jane");
        userToCreate.setLastName("Smith");
        userToCreate.setEmail("jane@example.com");
        userToCreate.setPassword("pass");
        userToCreate.setRole(User.Role.USER);
        userToCreate.setStatus(User.Status.ACTIVE);

        ResponseEntity<User> response = userController.createUser(userToCreate);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody()); // Check for null before accessing
        assertEquals(testUser.getEmail(), response.getBody().getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_Success() {
        // Setup mocks
        when(userRepository.findByEmail("john@example.com")).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtUtil.generateToken(testUser)).thenReturn("dummy.jwt.token");
        
        // Create request
        LoginRequest req = new LoginRequest();
        req.setEmail("john@example.com");
        req.setPassword("password");
        
        // Call login method
        ResponseEntity<?> response = userController.login(req);
        
        // Verify results
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof LoginResponse);
        
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertEquals("dummy.jwt.token", loginResponse.getToken());
        assertEquals(1, loginResponse.getUserId());
        assertEquals("John", loginResponse.getFirstName());
        assertEquals("john@example.com", loginResponse.getEmail());
        
        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("john@example.com");
        verify(jwtUtil).generateToken(testUser);
    }

    @Test
    void login_Failure() {
        // Setup mocks to throw exception during authentication
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new RuntimeException("Authentication failed"));
        
        // Create request
        LoginRequest req = new LoginRequest();
        req.setEmail("john@example.com");
        req.setPassword("wrongpassword");
        
        // Call login method
        ResponseEntity<?> response = userController.login(req);
        
        // Verify results
        assertEquals(401, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof String);
        assertTrue(((String)response.getBody()).contains("Authentication failed"));
        
        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(anyString());
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    void login_UserNotFound() {
        // Setup mocks
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByEmail("john@example.com")).thenReturn(null);
        
        // Create request
        LoginRequest req = new LoginRequest();
        req.setEmail("john@example.com");
        req.setPassword("password");
        
        // Call login method
        ResponseEntity<?> response = userController.login(req);
        
        // Verify results
        assertEquals(402, response.getStatusCode().value());
        assertEquals("User not found", response.getBody());
        
        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("john@example.com");
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    void updateUser_Found_ShouldUpdate() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updatedUser = new User();
        updatedUser.setEmail("new@example.com");
        updatedUser.setFirstName("New");
        updatedUser.setLastName("Name");
        updatedUser.setDateOfBirth(LocalDate.of(1995, 5, 5));
        updatedUser.setPhoneNumber("5555555555");
        updatedUser.setRole(User.Role.ADMIN);
        updatedUser.setStatus(User.Status.INACTIVE);

        ResponseEntity<String> response = userController.updateUser(1, updatedUser);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("User updated successfully", response.getBody());
        verify(userRepository).findById(1);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_NotFound_ShouldReturn404() {
        when(userRepository.findById(2)).thenReturn(Optional.empty());
        User updatedUser = new User();
        ResponseEntity<String> response = userController.updateUser(2, updatedUser);
        assertEquals(404, response.getStatusCode().value());
        assertEquals("User not found", response.getBody());
        verify(userRepository).findById(2);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_Found_ShouldDelete() {
        when(userRepository.existsById(1)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1);
        ResponseEntity<String> response = userController.deleteUser(1);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("User deleted successfully", response.getBody());
        verify(userRepository).existsById(1);
        verify(userRepository).deleteById(1);
    }

    @Test
    void deleteUser_NotFound_ShouldReturn404() {
        when(userRepository.existsById(2)).thenReturn(false);
        ResponseEntity<String> response = userController.deleteUser(2);
        assertEquals(404, response.getStatusCode().value());
        assertEquals("User not found", response.getBody());
        verify(userRepository).existsById(2);
        verify(userRepository, never()).deleteById(2);
    }
}
