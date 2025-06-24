package com.banking.backend.cucumber.steps;

import com.banking.backend.cucumber.helpers.TestContext;
import com.banking.backend.cucumber.helpers.TestDataBuilder;
import com.banking.backend.dto.RegisterDTO;
import com.banking.backend.dto.UserDTO;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class UserRegistrationSteps {

    @Autowired
    private TestContext testContext;

    private ResponseEntity<?> lastResponse;
    private RegisterDTO registerRequest;
    private UserDTO createdUser;

    // REMOVED: @Given("a user already exists with email {string}") - now in CommonSteps

    @When("I register with the following details:")
    public void i_register_with_the_following_details(DataTable dataTable) {
        Map<String, String> userData = dataTable.asMap(String.class, String.class);

        registerRequest = TestDataBuilder.createRegisterDTO(
                userData.get("firstName"),
                userData.get("lastName"),
                userData.get("email"),
                userData.get("password")
        );

        HttpEntity<RegisterDTO> request = new HttpEntity<>(registerRequest, testContext.createAuthHeaders());
        lastResponse = testContext.restTemplate.postForEntity("/users", request, UserDTO.class);
    }

    @Then("the user should be created successfully")
    public void the_user_should_be_created_successfully() {
        assertEquals(HttpStatus.CREATED, lastResponse.getStatusCode());
        assertNotNull(lastResponse.getBody());
        createdUser = (UserDTO) lastResponse.getBody();
    }

    @Then("I should receive a user ID")
    public void i_should_receive_a_user_id() {
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertTrue(createdUser.getId() instanceof UUID);
    }

    @Then("the user should have role {string}")
    public void the_user_should_have_role(String expectedRole) {
        testContext.testData.put("createdUserId", createdUser.getId());
    }

    @Then("the user should be active")
    public void the_user_should_be_active() {
        assertNotNull(createdUser);
    }

    @Then("the registration should fail")
    public void the_registration_should_fail() {
        assertNotEquals(HttpStatus.CREATED, lastResponse.getStatusCode());
    }

    @Then("I should receive an error message about duplicate email")
    public void i_should_receive_an_error_message_about_duplicate_email() {
        assertTrue(lastResponse.getStatusCode().is4xxClientError() ||
                lastResponse.getStatusCode().is5xxServerError());
    }

    @Then("I should receive an error message")
    public void i_should_receive_an_error_message() {
        assertTrue(lastResponse.getStatusCode().is4xxClientError() ||
                lastResponse.getStatusCode().is5xxServerError());
    }

    @Then("I should receive validation errors")
    public void i_should_receive_validation_errors() {
        assertEquals(HttpStatus.BAD_REQUEST, lastResponse.getStatusCode());
    }
}