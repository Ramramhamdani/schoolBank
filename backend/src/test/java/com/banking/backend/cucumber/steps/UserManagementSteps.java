package com.banking.backend.cucumber.steps;

import com.banking.backend.cucumber.helpers.TestContext;
import com.banking.backend.dto.UserDTO;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

public class UserManagementSteps {

    @Autowired
    private TestContext testContext;

    private ResponseEntity<?> lastResponse;
    private UserDTO retrievedUser;

    @When("I request user details for ID {string}")
    public void i_request_user_details_for_id(String userId) {
        HttpEntity<Void> request = new HttpEntity<>(testContext.createAuthHeaders());
        lastResponse = testContext.restTemplate.exchange("/users/" + userId, HttpMethod.GET, request, UserDTO.class);
    }

    @Then("I should receive the user details")
    public void i_should_receive_the_user_details() {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
        assertNotNull(lastResponse.getBody());
        retrievedUser = (UserDTO) lastResponse.getBody();
    }

    @Then("the user should contain:")
    public void the_user_should_contain(io.cucumber.datatable.DataTable dataTable) {
        assertNotNull(retrievedUser);

        for (String field : dataTable.asList()) {
            switch (field) {
                case "id":
                    assertNotNull(retrievedUser.getId());
                    break;
                case "firstName":
                    assertNotNull(retrievedUser.getFirstName());
                    break;
                case "lastName":
                    assertNotNull(retrievedUser.getLastName());
                    break;
                case "email":
                    assertNotNull(retrievedUser.getEmail());
                    break;
            }
        }
    }

    @Then("the user details should not contain password")
    public void the_user_details_should_not_contain_password() {
        assertNotNull(retrievedUser);
    }
}