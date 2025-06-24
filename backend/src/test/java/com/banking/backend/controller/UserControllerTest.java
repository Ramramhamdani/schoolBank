package com.banking.backend.controller;

import com.banking.backend.model.User;
import com.banking.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private User testUser;
    private UUID testUserId;
    private String testUserIdString;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUserIdString = testUserId.toString();

        testUser = new User();
        testUser.setId(testUserId);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("hashedPassword123"); 
        // (password is set here on the entity, but the controller only
        //  returns a UserDTO, which does not serialize password.)

        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerUser_Success() throws Exception {
        // Given
        User inputUser = new User();
        inputUser.setFirstName("John");
        inputUser.setLastName("Doe");
        inputUser.setEmail("john.doe@example.com");
        inputUser.setPassword("password123");

        // We stub the service to return a fully‐populated User entity.
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testUser.getLastName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
                // ↓ no jsonPath("$.password") any more, because the controller returns UserDTO
    }

    @Test
    void registerUser_WithEmptyName() throws Exception {
        // Given
        User inputUser = new User();
        inputUser.setFirstName("");
        inputUser.setLastName("");
        inputUser.setEmail("john.doe@example.com");
        inputUser.setPassword("password123");

        User createdUser = new User();
        createdUser.setId(testUserId);
        createdUser.setFirstName("");
        createdUser.setLastName("");
        createdUser.setEmail("john.doe@example.com");
        createdUser.setPassword("hashedPassword123");

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value(""))
                .andExpect(jsonPath("$.lastName").value(""));
                // We still only check firstName/lastName fields here
    }

    @Test
    void registerUser_WithNullFields() throws Exception {
        // Given
        User inputUser = new User();
        inputUser.setFirstName(null);
        inputUser.setLastName(null);
        inputUser.setEmail(null);
        inputUser.setPassword(null);

        User createdUser = new User();
        createdUser.setId(testUserId);
        createdUser.setFirstName(null);
        createdUser.setLastName(null);
        createdUser.setEmail(null);
        createdUser.setPassword(null);

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(createdUser.getId().toString()));
                // We only assert on id, since firstName/lastName/email/password are all null
    }

    @Test
    void registerUser_EmptyRequestBody() throws Exception {
        // Given
        String emptyJson = "{}";

        User createdUser = new User();
        createdUser.setId(testUserId);

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(createdUser.getId().toString()));
    }

    @Test
    void registerUser_InvalidJson() throws Exception {
        // Given
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_Success() throws Exception {
        // Given
        when(userService.getUserById(eq(testUserIdString))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/users/{id}", testUserIdString))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testUser.getLastName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
                // ↓ no jsonPath("$.password") here, since the controller returns UserDTO
    }

    @Test
    void getUserById_NotFound() throws Exception {
        // Given
        String nonExistentId = UUID.randomUUID().toString();
        when(userService.getUserById(eq(nonExistentId))).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/users/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserById_WithValidUuidString() throws Exception {
        // Given
        String validUuidString = "550e8400-e29b-41d4-a716-446655440000";
        User user = new User();
        user.setId(UUID.fromString(validUuidString));
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.com");
        user.setPassword("hashedPassword456");

        when(userService.getUserById(eq(validUuidString))).thenReturn(user);

        // When & Then
        mockMvc.perform(get("/users/{id}", validUuidString))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(validUuidString))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"));
                // Again: no password check, because response is a UserDTO
    }

    @Test
    void getUserById_WithNonUuidString() throws Exception {
        // Given
        String nonUuidString = "not-a-uuid";
        when(userService.getUserById(eq(nonUuidString))).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/users/{id}", nonUuidString))
                .andExpect(status().isNotFound()); 
                // The mock returns null, so controller returns 404. No unnecessary stub error.
    }

    @Test
    void getUserById_WithEmptyString_ShouldReturn405() throws Exception {
        // Given
        String emptyId = "";

        // When & Then
        mockMvc.perform(get("/users/{id}", emptyId))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void getUserById_WithNumericString() throws Exception {
        // Given
        String numericId = "12345";
        when(userService.getUserById(eq(numericId))).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/users/{id}", numericId))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerUser_ServiceReturnsNull() throws Exception {
        // Given
        User inputUser = new User();
        inputUser.setFirstName("John");
        inputUser.setLastName("Doe");
        inputUser.setEmail("john.doe@example.com");
        inputUser.setPassword("password123");

        when(userService.createUser(any(User.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated())
                .andExpect(content().string("")); 
                // When service returns null, controller sends 201 + empty body
    }

    @Test
    void registerUser_WithSpecialCharacters() throws Exception {
        // Given
        User inputUser = new User();
        inputUser.setFirstName("João José");
        inputUser.setLastName("Ñoño");
        inputUser.setEmail("joão@exãmple.com");
        inputUser.setPassword("pássword@123!");

        User createdUser = new User();
        createdUser.setId(testUserId);
        createdUser.setFirstName("João José");
        createdUser.setLastName("Ñoño");
        createdUser.setEmail("joão@exãmple.com");
        createdUser.setPassword("hashedPássword@123!");

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("João José"))
                .andExpect(jsonPath("$.lastName").value("Ñoño"))
                .andExpect(jsonPath("$.email").value("joão@exãmple.com"));
                // Again, no password assertion here
    }
}
