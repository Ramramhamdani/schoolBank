package com.banking.backend.controller;

import com.banking.backend.dto.JwtResponseDTO;
import com.banking.backend.dto.LoginDTO;
import com.banking.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login endpoint: accepts email and password,
     * returns JWT token if successful, 401 error if not.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        try {
            String token = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(new JwtResponseDTO(token));
        } catch (RuntimeException e) {
            // Return unauthorized with error message
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}