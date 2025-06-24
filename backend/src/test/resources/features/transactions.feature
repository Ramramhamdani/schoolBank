Feature: Money Transactions
  As a banking customer
  I want to transfer money between accounts
  So that I can manage my finances

  Background:
    Given the banking application is running
    And I am authenticated as a valid user
    And an account exists with IBAN "NL01BANK0123456789" and balance 1000.00
    And an account exists with IBAN "NL02BANK0987654321" and balance 500.00
    And a user exists with ID "550e8400-e29b-41d4-a716-446655440001"

  Scenario: Successful money transfer
    When I create a transaction with the following details:
      | fromIban         | NL01BANK0123456789                     |
      | toIban           | NL02BANK0987654321                     |
      | amount           | 200.00                                 |
      | performingUserId | 550e8400-e29b-41d4-a716-446655440001   |
      | description      | Transfer to savings                    |
    Then the transaction should be created successfully
    And the transaction should have a valid ID
    And the source account balance should be updated
    And the destination account balance should be updated
    And the transaction should be recorded with correct details

  Scenario: Transfer to same account
    When I create a transaction with the following details:
      | fromIban         | NL01BANK0123456789     |
      | toIban           | NL01BANK0123456789     |
      | amount           | 100.00                 |
      | performingUserId | 550e8400-e29b-41d4-a716-446655440001 |
    Then the transaction should fail with status 400
    And I should receive error message "Cannot transfer to the same account"

  Scenario: Transfer with zero amount
    When I create a transaction with the following details:
      | fromIban         | NL01BANK0123456789     |
      | toIban           | NL02BANK0987654321     |
      | amount           | 0                      |
      | performingUserId | 550e8400-e29b-41d4-a716-446655440001 |
    Then the transaction should fail with status 400
    And I should receive error message "Transaction amount must be greater than zero"

  Scenario: Transfer with negative amount
    When I create a transaction with the following details:
      | fromIban         | NL01BANK0123456789     |
      | toIban           | NL02BANK0987654321     |
      | amount           | -100.00                |
      | performingUserId | 550e8400-e29b-41d4-a716-446655440001 |
    Then the transaction should fail with status 400
    And I should receive error message "Transaction amount must be positive"

  Scenario: Transfer with insufficient funds
    When I create a transaction with the following details:
      | fromIban         | NL01BANK0123456789     |
      | toIban           | NL02BANK0987654321     |
      | amount           | 1500.00                |
      | performingUserId | 550e8400-e29b-41d4-a716-446655440001 |
    Then the transaction should fail with status 400
    And I should receive error message "Insufficient funds in account"

  Scenario: Transfer with non-existent source account
    When I create a transaction with the following details:
      | fromIban         | NL99FAKE0000000000     |
      | toIban           | NL02BANK0987654321     |
      | amount           | 100.00                 |
      | performingUserId | 550e8400-e29b-41d4-a716-446655440001 |
    Then the transaction should fail with status 400
    And I should receive error message "Source account not found"

  Scenario: Transfer with non-existent destination account
    When I create a transaction with the following details:
      | fromIban         | NL01BANK0123456789     |
      | toIban           | NL99FAKE0000000000     |
      | amount           | 100.00                 |
      | performingUserId | 550e8400-e29b-41d4-a716-446655440001 |
    Then the transaction should fail with status 400
    And I should receive error message "Destination account not found"

  Scenario: Transfer with non-existent performing user
    When I create a transaction with the following details:
      | fromIban         | NL01BANK0123456789                     |
      | toIban           | NL02BANK0987654321                     |
      | amount           | 100.00                                 |
      | performingUserId | 550e8400-e29b-41d4-a716-446655440999   |
    Then the transaction should fail with status 400
    And I should receive error message "Performing user not found"

  Scenario: Get transaction history for an account
    Given the account "NL01BANK0123456789" has 3 existing transactions
    When I request transaction history for account "550e8400-e29b-41d4-a716-446655440000"
    Then I should receive a list of 3 transactions
    And each transaction should contain all required fields
    And transactions should be properly formatted

  Scenario: Get transaction history for account with no transactions
    Given an account with ID "550e8400-e29b-41d4-a716-446655440003" has no transactions
    When I request transaction history for account "550e8400-e29b-41d4-a716-446655440003"
    Then I should receive an empty transaction list