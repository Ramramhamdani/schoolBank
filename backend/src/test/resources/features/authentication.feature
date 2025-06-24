Feature: User Authentication
  As a registered user
  I want to login to my account
  So that I can access banking services

  Background:
    Given the banking application is running
    And a user exists with email "test@example.com" and password "password123"

  Scenario: Successful login
    When I login with email "test@example.com" and password "password123"
    Then I should receive a JWT token
    And the login should be successful

  Scenario: Login with wrong password
    When I login with email "test@example.com" and password "wrongpassword"
    Then the login should fail with status 401
    And I should receive an unauthorized error message

  Scenario: Login with non-existent email
    When I login with email "nonexistent@example.com" and password "password123"
    Then the login should fail with status 401
    And I should receive an unauthorized error message

  Scenario: Login with empty credentials
    When I login with empty email and password
    Then the login should fail with status 401