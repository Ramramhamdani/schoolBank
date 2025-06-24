Feature: Error Handling
  As a API consumer
  I want to receive proper error messages
  So that I can handle errors appropriately

  Background:
    Given the banking application is running
    And I am authenticated as a valid user

  Scenario: Invalid UUID format in path parameter
    When I request account details for invalid ID "not-a-uuid"
    Then I should receive a 400 bad request response

  Scenario: Invalid JSON in request body
    When I send invalid JSON to create account endpoint
    Then I should receive a 400 bad request response

  Scenario: Missing required fields in request
    When I create a transaction without required fields
    Then I should receive a 400 bad request response
    And I should receive appropriate validation error messages