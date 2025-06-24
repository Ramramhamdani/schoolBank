Feature: User Registration
  As a new customer
  I want to register for a banking account
  So that I can access banking services

  Scenario: Successful user registration
    Given the banking application is running
    When I register with the following details:
      | firstName | John        |
      | lastName  | Doe         |
      | email     | john@test.com |
      | password  | password123 |
    Then the user should be created successfully
    And I should receive a user ID
    And the user should have role "CUSTOMER"
    And the user should be active

  Scenario: Registration with duplicate email
    Given a user already exists with email "existing@test.com"
    When I register with the following details:
      | firstName | Jane        |
      | lastName  | Smith       |
      | email     | existing@test.com |
      | password  | password123 |
    Then the registration should fail
    And I should receive an error message

  Scenario: Registration with invalid data
    Given the banking application is running
    When I register with the following details:
      | firstName |             |
      | lastName  | Smith       |
      | email     | invalid-email |
      | password  | 123         |
    Then the registration should fail
    And I should receive validation errors