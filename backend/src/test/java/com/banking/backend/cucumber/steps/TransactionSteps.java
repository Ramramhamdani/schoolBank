package com.banking.backend.cucumber.steps;

import com.banking.backend.cucumber.helpers.TestContext;
import com.banking.backend.cucumber.helpers.TestDataBuilder;
import com.banking.backend.dto.TransactionDTO;
import com.banking.backend.model.Account;
import com.banking.backend.model.AccountType;
import com.banking.backend.model.Transaction;
import com.banking.backend.model.User;
import com.banking.backend.repository.TransactionRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class TransactionSteps {

    @Autowired
    private TestContext testContext;

    @Autowired
    private TransactionRepository transactionRepository;

    private ResponseEntity<?> lastResponse;
    private TransactionDTO transactionRequest;
    private TransactionDTO createdTransaction;
    private List<TransactionDTO> transactionList;

    @Given("an account exists with IBAN {string} and balance {double}")
    public void an_account_exists_with_iban_and_balance(String iban, double balance) {
        Account account = new Account();
        account.setIban(iban);
        account.setBalance(balance);
        account.setTypeOfAccount(AccountType.CURRENT);
        account.setDateOfOpening(LocalDate.now());
        account.setAbsoluteLimit(0.0);
        account.setActive(true);

        String testEmail = "test.user@example.com";
        User user = testContext.userRepository.findByEmail(testEmail).orElse(null);
        if (user != null) {
            account.setUser(user);
            testContext.accountRepository.save(account);
            testContext.testData.put("account_" + iban, account);
        }
    }

    // REMOVED: @Given("a user exists with ID {string}") - now in CommonSteps

    @Given("the account {string} has {int} existing transactions")
    public void the_account_has_existing_transactions(String iban, int transactionCount) {
        Account account = (Account) testContext.testData.get("account_" + iban);
        if (account != null) {
            for (int i = 0; i < transactionCount; i++) {
                Transaction transaction = new Transaction();
                transaction.setFromAccount(account);
                transaction.setToAccount(account);
                transaction.setAmount(10.0 + i);
                transactionRepository.save(transaction);
            }
        }
    }

    @Given("an account with ID {string} has no transactions")
    public void an_account_with_id_has_no_transactions(String accountId) {
        testContext.testData.put("emptyAccountId", UUID.fromString(accountId));
    }

    @When("I create a transaction with the following details:")
    public void i_create_a_transaction_with_the_following_details(DataTable dataTable) {
        Map<String, String> transactionData = dataTable.asMap(String.class, String.class);

        UUID performingUserId = transactionData.containsKey("performingUserId") ?
                UUID.fromString(transactionData.get("performingUserId")) : null;

        transactionRequest = TestDataBuilder.createTransactionDTO(
                transactionData.get("fromIban"),
                transactionData.get("toIban"),
                Double.parseDouble(transactionData.get("amount")),
                performingUserId,
                transactionData.getOrDefault("description", "Test transaction")
        );

        HttpEntity<TransactionDTO> request = new HttpEntity<>(transactionRequest, testContext.createAuthHeaders());
        lastResponse = testContext.restTemplate.postForEntity("/transactions", request, TransactionDTO.class);
    }

    @When("I request transaction history for account {string}")
    public void i_request_transaction_history_for_account(String accountId) {
        HttpEntity<Void> request = new HttpEntity<>(testContext.createAuthHeaders());
        lastResponse = testContext.restTemplate.exchange("/accounts/" + accountId + "/transactions",
                HttpMethod.GET, request, new ParameterizedTypeReference<List<TransactionDTO>>() {});
    }

    @When("I create a transaction without required fields")
    public void i_create_a_transaction_without_required_fields() {
        TransactionDTO incompleteTransaction = new TransactionDTO();

        HttpEntity<TransactionDTO> request = new HttpEntity<>(incompleteTransaction, testContext.createAuthHeaders());
        lastResponse = testContext.restTemplate.postForEntity("/transactions", request, String.class);
    }

    @Then("the transaction should be created successfully")
    public void the_transaction_should_be_created_successfully() {
        assertEquals(HttpStatus.CREATED, lastResponse.getStatusCode());
        assertNotNull(lastResponse.getBody());
        createdTransaction = (TransactionDTO) lastResponse.getBody();
    }

    @Then("the transaction should have a valid ID")
    public void the_transaction_should_have_a_valid_id() {
        assertNotNull(createdTransaction);
        assertNotNull(createdTransaction.getId());
    }

    @Then("the source account balance should be updated")
    public void the_source_account_balance_should_be_updated() {
        Account sourceAccount = (Account) testContext.testData.get("account_" + transactionRequest.getFromIban());
        if (sourceAccount != null) {
            Account updated = testContext.accountRepository.findById(sourceAccount.getId()).orElse(null);
            assertNotNull(updated);
        }
    }

    @Then("the destination account balance should be updated")
    public void the_destination_account_balance_should_be_updated() {
        Account destAccount = (Account) testContext.testData.get("account_" + transactionRequest.getToIban());
        if (destAccount != null) {
            Account updated = testContext.accountRepository.findById(destAccount.getId()).orElse(null);
            assertNotNull(updated);
        }
    }

    @Then("the transaction should be recorded with correct details")
    public void the_transaction_should_be_recorded_with_correct_details() {
        assertNotNull(createdTransaction);
        assertEquals(transactionRequest.getFromIban(), createdTransaction.getFromIban());
        assertEquals(transactionRequest.getToIban(), createdTransaction.getToIban());
        assertEquals(transactionRequest.getAmount(), createdTransaction.getAmount(), 0.01);
        assertNotNull(createdTransaction.getDateOfExecution());
    }

    @Then("the transaction should fail with status {int}")
    public void the_transaction_should_fail_with_status(int expectedStatus) {
        assertEquals(expectedStatus, lastResponse.getStatusCodeValue());
    }

    @Then("I should receive error message {string}")
    public void i_should_receive_error_message(String expectedMessage) {
        assertTrue(lastResponse.getStatusCode().is4xxClientError());
        String responseBody = lastResponse.getBody().toString();
        assertEquals(expectedMessage, responseBody);
    }

    @Then("I should receive a list of {int} transactions")
    public void i_should_receive_a_list_of_transactions(int expectedCount) {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
        assertNotNull(lastResponse.getBody());
        transactionList = (List<TransactionDTO>) lastResponse.getBody();
        assertEquals(expectedCount, transactionList.size());
    }

    @Then("each transaction should contain all required fields")
    public void each_transaction_should_contain_all_required_fields() {
        assertNotNull(transactionList);
        for (TransactionDTO transaction : transactionList) {
            assertNotNull(transaction.getId());
            assertNotNull(transaction.getFromIban());
            assertNotNull(transaction.getToIban());
            assertTrue(transaction.getAmount() > 0);
            assertNotNull(transaction.getDateOfExecution());
        }
    }

    @Then("transactions should be properly formatted")
    public void transactions_should_be_properly_formatted() {
        for (TransactionDTO transaction : transactionList) {
            assertTrue(transaction.getFromIban().matches("NL\\d{2}[A-Z]{4}\\d{10}"));
            assertTrue(transaction.getToIban().matches("NL\\d{2}[A-Z]{4}\\d{10}"));
            assertTrue(transaction.getAmount() > 0);
        }
    }

    @Then("I should receive an empty transaction list")
    public void i_should_receive_an_empty_transaction_list() {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
        assertNotNull(lastResponse.getBody());
        transactionList = (List<TransactionDTO>) lastResponse.getBody();
        assertTrue(transactionList.isEmpty());
    }
}