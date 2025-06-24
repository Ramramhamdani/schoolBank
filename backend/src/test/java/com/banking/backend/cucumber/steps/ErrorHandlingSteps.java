package com.banking.backend.cucumber.steps;

import com.banking.backend.cucumber.helpers.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ErrorHandlingSteps {

    @Autowired
    private TestContext testContext;

    private ResponseEntity<?> errorResponse;

    @When("I request account details for invalid ID {string}")
    public void i_request_account_details_for_invalid_id(String invalidId) {
        HttpEntity<Void> request = new HttpEntity<>(testContext.createAuthHeaders());
        errorResponse = testContext.restTemplate.exchange("/accounts/" + invalidId, HttpMethod.GET, request, String.class);
    }

    @When("I send invalid JSON to create account endpoint")
    public void i_send_invalid_json_to_create_account_endpoint() {
        HttpEntity<String> request = new HttpEntity<>("{invalid json", testContext.createAuthHeaders());
        errorResponse = testContext.restTemplate.postForEntity("/accounts", request, String.class);
    }

    @Then("I should receive a {int} bad request response")
    public void i_should_receive_a_bad_request_response(int statusCode) {
        assertEquals(statusCode, errorResponse.getStatusCodeValue());
    }

    @Then("I should receive appropriate validation error messages")
    public void i_should_receive_appropriate_validation_error_messages() {
        assertTrue(errorResponse.getStatusCode().is4xxClientError());
        assertNotNull(errorResponse.getBody());
    }
}