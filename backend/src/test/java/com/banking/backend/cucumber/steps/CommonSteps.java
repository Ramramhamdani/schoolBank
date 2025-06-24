package com.banking.backend.cucumber.steps;

import com.banking.backend.cucumber.helpers.TestContext;
import com.banking.backend.cucumber.helpers.TestDataBuilder;
import com.banking.backend.dto.*;
import com.banking.backend.mapper.UserMapper;
import com.banking.backend.model.User;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class CommonSteps {

    @Autowired
    private TestContext testContext;

    @Given("the banking application is running")
    public void the_banking_application_is_running() {
        // Application is running via @SpringBootTest in CucumberSpringConfiguration
    }

    @Given("I am authenticated as a valid user")
    public void i_am_authenticated_as_a_valid_user() throws Exception {
        // Create a test user if not exists
        String testEmail = "test.user@example.com";
        if (testContext.userRepository.findByEmail(testEmail).isEmpty()) {
            RegisterDTO registerDTO = TestDataBuilder.createRegisterDTO("Test", "User", testEmail, "password123");
            User userEntity = UserMapper.toEntity(registerDTO);
            testContext.userService.createUser(userEntity);
        }

        // Login to get JWT token
        LoginDTO loginDTO = new LoginDTO(testEmail, "password123");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginDTO> request = new HttpEntity<>(loginDTO, headers);

        ResponseEntity<JwtResponseDTO> response = testContext.restTemplate.postForEntity("/auth/login", request, JwtResponseDTO.class);
        if (response.getBody() != null) {
            testContext.jwtToken = response.getBody().getToken();
        }

    }

    @Given("a user exists with ID {string}")
    public void a_user_exists_with_id(String userId) {
        UUID userUuid = UUID.fromString(userId);

        // Check if user already exists
        User existingUser = testContext.userRepository.findById(userUuid).orElse(null);
        if (existingUser == null) {
            // Create new user
            existingUser = new User();
            existingUser.setId(userUuid);
            existingUser.setFirstName("Test");
            existingUser.setLastName("User");
            existingUser.setEmail("testuser" + userId.replace("-", "") + "@example.com");
            existingUser.setPassword("hashedPassword");
            existingUser.setActive(true);
            testContext.userRepository.save(existingUser);
        }
        testContext.testData.put("testUserId", userUuid);
    }

    @Given("a user exists with email {string} and password {string}")
    public void a_user_exists_with_email_and_password(String email, String password) {
        if (testContext.userRepository.findByEmail(email).isEmpty()) {
            RegisterDTO registerDTO = TestDataBuilder.createRegisterDTO("Test", "User", email, password);
            User userEntity = UserMapper.toEntity(registerDTO);
            testContext.userService.createUser(userEntity);
        }
    }

    @Given("a user already exists with email {string}")
    public void a_user_already_exists_with_email(String email) {
        RegisterDTO existingUser = TestDataBuilder.createRegisterDTO("Existing", "User", email, "password123");
        HttpEntity<RegisterDTO> request = new HttpEntity<>(existingUser, testContext.createAuthHeaders());
        testContext.restTemplate.postForEntity("/users", request, UserDTO.class);
    }

    @Given("a user exists with IBAN {string}")
    public void a_user_exists_with_iban(String iban) {
        String email = iban; // Your service uses email lookup
        if (testContext.userRepository.findByEmail(email).isEmpty()) {
            RegisterDTO registerDTO = TestDataBuilder.createRegisterDTO("Test", "Customer", email, "password123");
            User userEntity = UserMapper.toEntity(registerDTO);
            testContext.userService.createUser(userEntity);
        }
    }
}