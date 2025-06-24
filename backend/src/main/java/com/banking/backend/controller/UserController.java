package com.banking.backend.controller;

import com.banking.backend.dto.RegisterDTO;
import com.banking.backend.dto.UserDTO;
import com.banking.backend.mapper.UserMapper;
import com.banking.backend.model.User;
import com.banking.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Accept RegisterDTO → map to User → save → map to UserDTO
    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterDTO dto) {
        User entityToSave = UserMapper.toEntity(dto);
        User savedEntity  = userService.createUser(entityToSave);
        UserDTO response  = UserMapper.toDTO(savedEntity);
        return ResponseEntity.status(201).body(response);
    }

    // Look up User by ID → map to UserDTO
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        User userEntity = userService.getUserById(id);
        if (userEntity == null) {
            return ResponseEntity.notFound().build();
        }
        UserDTO response = UserMapper.toDTO(userEntity);
        return ResponseEntity.ok(response);
    }
}