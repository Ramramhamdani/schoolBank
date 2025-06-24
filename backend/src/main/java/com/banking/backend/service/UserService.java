package com.banking.backend.service;

import com.banking.backend.model.User;
import com.banking.backend.repository.UserRepository;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        if (user == null) {
        throw new IllegalArgumentException("User cannot be null");
        }
        // Hash the password before saving
        String rawPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    public User getUserById(String id) {
        if (id == null || id.isEmpty()) {
        throw new IllegalArgumentException("User id cannot be null or empty");
        }
        return userRepository.findById(UUID.fromString(id)).orElse(null);
    }
}