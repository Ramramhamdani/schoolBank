package com.banking.backend.service;

import com.banking.backend.model.User;
import com.banking.backend.repository.UserRepository;
import com.banking.backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        jwtUtil = mock(JwtUtil.class);
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void login_withValidCredentials_returnsToken() {
        String rawPassword = "password123";
        String hashedPassword = passwordEncoder.encode(rawPassword);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPassword(hashedPassword);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user.getId())).thenReturn("valid.jwt.token");

        String token = authService.login("test@example.com", rawPassword);

        assertNotNull(token);
        assertEquals("valid.jwt.token", token);
    }

    @Test
    void login_withWrongPassword_throwsException() {
        User user = new User();
        user.setPassword(passwordEncoder.encode("correctPassword"));
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login("test@example.com", "wrongPassword");
        });

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void login_withNonExistentUser_throwsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login("nonexistent@example.com", "anyPassword");
        });

        assertEquals("Invalid email or password", exception.getMessage());
    }
}