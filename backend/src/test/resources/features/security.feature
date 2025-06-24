Feature: API Security
  As a system administrator
  I want to ensure proper authentication and authorization
  So that the banking system is secure

  Scenario: Access protected endpoint without authentication
    Given the banking application is running
    When I try to access "/accounts" without authentication
    Then I should receive a 401 unauthorized response

  Scenario: Access protected endpoint with invalid token
    Given the banking application is running
    When I try to access "/accounts" with an invalid JWT token
    Then I should receive a 401 unauthorized response

  Scenario: Access user-specific data with valid authentication
    Given the banking application is running
    And I am authenticated as a valid user
    When I access a protected endpoint
    Then I should receive a successful response