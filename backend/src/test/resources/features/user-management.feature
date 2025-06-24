Feature: User Management
  As a system administrator or user
  I want to manage user information
  So that I can maintain user accounts

  Background:
    Given the banking application is running
    And I am authenticated as a valid user

  Scenario: Get user by ID
    Given a user exists with ID "550e8400-e29b-41d4-a716-446655440001"
    When I request user details for ID "550e8400-e29b-41d4-a716-446655440001"
    Then I should receive the user details
    And the user should contain:
      | id        |
      | firstName |
      | lastName  |
      | email     |
    And the user details should not contain password

  Scenario: Get non-existent user
    When I request user details for ID "550e8400-e29b-41d4-a716-446655440999"
    Then I should receive a 404 not found response