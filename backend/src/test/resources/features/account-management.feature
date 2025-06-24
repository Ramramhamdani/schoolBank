Feature: Account Management
  As a banking customer
  I want to manage my accounts
  So that I can perform banking operations

  Background:
    Given the banking application is running
    And I am authenticated as a valid user
    And a user exists with IBAN "test.user@example.com"

  Scenario: Create a new savings account
    When I create an account with the following details:
      | requestedAccountType | SAVINGS               |
      | customerIban        | test.user@example.com    |
    Then the account should be created successfully
    And the account should have type "SAVINGS"
    And the account should be active
    And the account should have a valid IBAN
    And the account should have zero balance initially

  Scenario: Create a new current account
    When I create an account with the following details:
      | requestedAccountType | CURRENT               |
      | customerIban        | test.user@example.com    |
    Then the account should be created successfully
    And the account should have type "CURRENT"

  Scenario: Get all accounts
    Given I have 2 existing accounts
    When I request all accounts
    Then I should receive a list of 2 accounts
    And each account should contain valid account information

  Scenario: Get account by ID
    Given I have an existing account with ID "550e8400-e29b-41d4-a716-446655440000"
    When I request account details for ID "550e8400-e29b-41d4-a716-446655440000"
    Then I should receive the account details
    And the account should contain all required fields

  Scenario: Get accounts for a specific user
    Given a user with ID "550e8400-e29b-41d4-a716-446655440001" has 3 accounts
    When I request accounts for user "550e8400-e29b-41d4-a716-446655440001"
    Then I should receive a list of 3 accounts
    And all accounts should belong to that user

  Scenario: Delete an account
    Given I have an existing account with ID "550e8400-e29b-41d4-a716-446655440002"
    When I delete the account with ID "550e8400-e29b-41d4-a716-446655440002"
    Then the account should be deleted successfully
    And the account should no longer exist

  Scenario: Get non-existent account
    When I request account details for ID "550e8400-e29b-41d4-a716-446655440999"
    Then I should receive a 404 not found response