package com.banking.backend.cucumber.steps;

import com.banking.backend.cucumber.helpers.TestContext;
import com.banking.backend.dto.JwtResponseDTO;
import com.banking.backend.dto.LoginDTO;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

public class AuthenticationSteps {

    @Autowired
    private TestContext testContext;

    private ResponseEntity<?> loginResponse;
    private String receivedToken;

    @When("I login with email {string} and password {string}")
    public void i_login_with_email_and_password(String email, String password) {
        LoginDTO loginDTO = new LoginDTO(email, password);
        HttpEntity<LoginDTO> request = new HttpEntity<>(loginDTO, testContext.createAuthHeaders());
        loginResponse = testContext.restTemplate.postForEntity("/auth/login", request, JwtResponseDTO.class);
    }

    @When("I login with empty email and password")
    public void i_login_with_empty_email_and_password() {
        LoginDTO loginDTO = new LoginDTO("", "");
        HttpEntity<LoginDTO> request = new HttpEntity<>(loginDTO, testContext.createAuthHeaders());
        loginResponse = testContext.restTemplate.postForEntity("/auth/login", request, String.class);
    }

    @Then("I should receive a JWT token")
    public void i_should_receive_a_jwt_token() {
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        JwtResponseDTO responseBody = (JwtResponseDTO) loginResponse.getBody();
        receivedToken = responseBody.getToken();
        assertNotNull(receivedToken);
        assertTrue(receivedToken.length() > 0);
    }

    @Then("the login should be successful")
    public void the_login_should_be_successful() {
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
    }

    @Then("the login should fail with status {int}")
    public void the_login_should_fail_with_status(int expectedStatus) {
        assertEquals(expectedStatus, loginResponse.getStatusCodeValue());
    }

    @Then("I should receive an unauthorized error message")
    public void i_should_receive_an_unauthorized_error_message() {
        assertEquals(HttpStatus.UNAUTHORIZED, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
    }
}