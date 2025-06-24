package com.banking.backend.service;

import com.banking.backend.model.User;
import com.banking.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // UserService instance with mocks injected
    private final UserService userService = new UserService(userRepository, passwordEncoder);

    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(testUserId);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
    }

    @Test
    public void testCreateUser_passwordIsHashed() {
        User user = new User();
        user.setFirstName("Alice");
        user.setLastName("Doe");
        user.setPassword("plainPassword123");
        user.setEmail("test@example.com");

        // When save() is called, just return the input user (simulate DB save)
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.createUser(user);

        assertNotNull(savedUser);
        // Password should be hashed — not the original plain password
        assertNotEquals("plainPassword123", savedUser.getPassword());
        // BCrypt should match the hashed password with the plain password
        assertTrue(passwordEncoder.matches("plainPassword123", savedUser.getPassword()));
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUser(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
        void createUser_WithNullUser_ShouldHandleGracefully() {
    // When & Then
    assertThrows(IllegalArgumentException.class, () -> {
        userService.createUser(null);
    });
    // Ensure repository.save(null) is NOT called
    verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_WithDuplicateUsername_ShouldHandleConstraintViolation() {
        // Given
        when(userRepository.save(any(User.class)))
                .thenThrow(new RuntimeException("Username already exists"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(testUser);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Given
        String userIdString = testUserId.toString();
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getUserById(userIdString);

        // Then
        assertNotNull(result);
        assertEquals(testUserId, result.getId());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(testUserId);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnNull() {
        // Given
        UUID nonExistentUserId = UUID.randomUUID();
        String nonExistentUserIdString = nonExistentUserId.toString();
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // When
        User result = userService.getUserById(nonExistentUserIdString);

        // Then
        assertNull(result);
        verify(userRepository, times(1)).findById(nonExistentUserId);
    }

    @Test
    void getUserById_WithInvalidUUIDFormat_ShouldThrowException() {
        // Given
        String invalidUuidString = "invalid-uuid-format";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(invalidUuidString);
        });
        verify(userRepository, never()).findById(any(UUID.class));
    }

    @Test
    void getUserById_WithNullId_ShouldThrowException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(null);
        });
        verify(userRepository, never()).findById(any(UUID.class));
    }

    @Test
    void getUserById_WithEmptyString_ShouldThrowException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById("");
        });
        verify(userRepository, never()).findById(any(UUID.class));
    }

    @Test
    void getUserById_ValidUUIDString_ShouldParseCorrectly() {
        // Given
        String validUuidString = "550e8400-e29b-41d4-a716-446655440000";
        UUID expectedUuid = UUID.fromString(validUuidString);
        User expectedUser = new User();
        expectedUser.setId(expectedUuid);
        expectedUser.setFirstName("Valid");
        expectedUser.setLastName("User");
        expectedUser.setEmail("valid@example.com");

        when(userRepository.findById(expectedUuid)).thenReturn(Optional.of(expectedUser));

        // When
        User result = userService.getUserById(validUuidString);

        // Then
        assertNotNull(result);
        assertEquals(expectedUuid, result.getId());
        assertEquals("Valid", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals("valid@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(expectedUuid);
    }
}