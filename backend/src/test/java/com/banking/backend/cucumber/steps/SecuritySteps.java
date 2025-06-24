package com.banking.backend.cucumber.steps;

import com.banking.backend.cucumber.helpers.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

public class SecuritySteps {

    @Autowired
    private TestContext testContext;

    private ResponseEntity<?> securityResponse;

    @When("I try to access {string} without authentication")
    public void i_try_to_access_without_authentication(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        // No authorization header
        HttpEntity<Void> request = new HttpEntity<>(headers);
        securityResponse = testContext.restTemplate.exchange(endpoint, HttpMethod.GET, request, String.class);
    }

    @When("I try to access {string} with an invalid JWT token")
    public void i_try_to_access_with_an_invalid_jwt_token(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("invalid.jwt.token");
        HttpEntity<Void> request = new HttpEntity<>(headers);
        securityResponse = testContext.restTemplate.exchange(endpoint, HttpMethod.GET, request, String.class);
    }

    @When("I access a protected endpoint")
    public void i_access_a_protected_endpoint() {
        HttpEntity<Void> request = new HttpEntity<>(testContext.createAuthHeaders());
        securityResponse = testContext.restTemplate.exchange("/accounts", HttpMethod.GET, request, String.class);
    }

    @Then("I should receive a {int} unauthorized response")
    public void i_should_receive_a_unauthorized_response(int statusCode) {
        assertEquals(statusCode, securityResponse.getStatusCodeValue());
    }

    @Then("I should receive a successful response")
    public void i_should_receive_a_successful_response() {
        assertTrue(securityResponse.getStatusCode().is2xxSuccessful());
    }
}