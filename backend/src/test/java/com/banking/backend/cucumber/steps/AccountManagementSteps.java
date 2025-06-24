package com.banking.backend.cucumber.steps;

import com.banking.backend.cucumber.helpers.TestContext;
import com.banking.backend.cucumber.helpers.TestDataBuilder;
import com.banking.backend.dto.AccountDTO;
import com.banking.backend.dto.AccountRequestDTO;
import com.banking.backend.dto.RegisterDTO;
import com.banking.backend.mapper.UserMapper;
import com.banking.backend.model.Account;
import com.banking.backend.model.AccountType;
import com.banking.backend.model.User;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@Rollback
public class AccountManagementSteps {

    @Autowired
    private TestContext testContext;

    private ResponseEntity<?> lastResponse;
    private AccountRequestDTO accountRequest;
    private AccountDTO createdAccount;
    private List<AccountDTO> accountList;

    @Given("I have {int} existing accounts")
    public void i_have_existing_accounts(int count) {
        // Clean up existing accounts to ensure test isolation
        testContext.accountRepository.deleteAll();

        String testEmail = "test.user@example.com";
        for (int i = 0; i < count; i++) {
            Account account = new Account();
            account.setIban(TestDataBuilder.generateTestIban("test" + i));
            account.setBalance(1000.0 + (i * 100));
            account.setTypeOfAccount(i % 2 == 0 ? AccountType.SAVINGS : AccountType.CURRENT);
            account.setDateOfOpening(LocalDate.now());
            account.setAbsoluteLimit(0.0);
            account.setActive(true);

            User user = testContext.userRepository.findByEmail(testEmail).orElse(null);
            if (user != null) {
                account.setUser(user);
                testContext.accountRepository.save(account);
            }
        }
    }

    @Given("I have an existing account with ID {string}")
    public void i_have_an_existing_account_with_id(String accountId) {
        // Don't use the provided ID - let JPA generate it
        Account account = new Account();
        // Remove: account.setId(uuid);
        account.setIban(TestDataBuilder.generateTestIban(accountId));
        account.setBalance(1500.0);
        account.setTypeOfAccount(AccountType.SAVINGS);
        account.setDateOfOpening(LocalDate.now());
        account.setAbsoluteLimit(0.0);
        account.setActive(true);

        String testEmail = "test.user@example.com";
        User user = testContext.userRepository.findByEmail(testEmail).orElse(null);
        if (user != null) {
            account.setUser(user);
            Account savedAccount = testContext.accountRepository.saveAndFlush(account);

            // Store the REAL generated ID for the test to use
            testContext.testData.put("realAccountId", savedAccount.getId().toString());
            System.out.println("Created account with real ID: " + savedAccount.getId());
        }
    }

    @Given("a user with ID {string} has {int} accounts")
    public void a_user_with_id_has_accounts(String userId, int accountCount) {
        // Don't try to force the UUID - let JPA generate it
        User user = new User();
        // Remove: user.setId(userUuid);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("testuser" + userId.replace("-", "") + "@example.com");
        user.setPassword("hashedPassword");
        user.setActive(true);
        user = testContext.userRepository.saveAndFlush(user);

        System.out.println("Created user with real ID: " + user.getId());

        // Clean existing accounts
        testContext.accountRepository.deleteAll();

        // Create accounts for this user
        for (int i = 0; i < accountCount; i++) {
            Account account = new Account();
            account.setIban(TestDataBuilder.generateTestIban("user" + user.getId() + "acc" + i));
            account.setBalance(500.0 + (i * 200));
            account.setTypeOfAccount(AccountType.SAVINGS);
            account.setDateOfOpening(LocalDate.now());
            account.setAbsoluteLimit(0.0);
            account.setActive(true);
            account.setUser(user);
            testContext.accountRepository.saveAndFlush(account);
        }
        // Store the REAL user ID for the test to use
        testContext.testData.put("realUserId", user.getId().toString());
        System.out.println("Created " + accountCount + " accounts for real user " + user.getId());
    }

    @When("I create an account with the following details:")
    public void i_create_an_account_with_the_following_details(DataTable dataTable) {
        Map<String, String> accountData = dataTable.asMap(String.class, String.class);

        accountRequest = TestDataBuilder.createAccountRequestDTO(
                accountData.get("requestedAccountType"),
                accountData.get("customerIban")
        );

        HttpHeaders headers = testContext.createAuthHeaders();
        HttpEntity<AccountRequestDTO> request = new HttpEntity<>(accountRequest, headers);

        // Change back to AccountDTO.class (remove the debug String.class)
        lastResponse = testContext.restTemplate.postForEntity("/accounts", request, AccountDTO.class);
    }

    @When("I request all accounts")
    public void i_request_all_accounts() {
        HttpEntity<Void> request = new HttpEntity<>(testContext.createAuthHeaders());
        lastResponse = testContext.restTemplate.exchange("/accounts", HttpMethod.GET, request,
                new ParameterizedTypeReference<List<AccountDTO>>() {});
    }

    @When("I request account details for ID {string}")
    public void i_request_account_details_for_id(String accountId) {
        String realAccountId = (String) testContext.testData.get("realAccountId");

        HttpEntity<Void> request = new HttpEntity<>(testContext.createAuthHeaders());
        lastResponse = testContext.restTemplate.exchange("/accounts/" + realAccountId,
                HttpMethod.GET, request, AccountDTO.class);
    }

    @When("I request accounts for user {string}")
    public void i_request_accounts_for_user(String userId) {
        String realUserId = (String) testContext.testData.get("realUserId");

        HttpEntity<Void> request = new HttpEntity<>(testContext.createAuthHeaders());
        lastResponse = testContext.restTemplate.exchange("/accounts/user/" + realUserId,
                HttpMethod.GET, request, new ParameterizedTypeReference<List<AccountDTO>>() {});

    }

    @When("I delete the account with ID {string}")
    public void i_delete_the_account_with_id(String accountId) {
        String realAccountId = (String) testContext.testData.get("realAccountId");
        HttpEntity<Void> request = new HttpEntity<>(testContext.createAuthHeaders());
        lastResponse = testContext.restTemplate.exchange("/accounts/" + realAccountId, HttpMethod.DELETE, request, Void.class);
    }

    @Then("the account should be created successfully")
    public void the_account_should_be_created_successfully() {
        assertEquals(HttpStatus.CREATED, lastResponse.getStatusCode());
        assertNotNull(lastResponse.getBody());
        createdAccount = (AccountDTO) lastResponse.getBody();
    }

    @Then("the account should have type {string}")
    public void the_account_should_have_type(String expectedType) {
        assertNotNull(createdAccount);
        assertEquals(expectedType, createdAccount.getTypeOfAccount());
    }

    @Then("the account should be active")
    public void the_account_should_be_active() {
        assertNotNull(createdAccount);
        assertTrue(createdAccount.isActive());
    }

    @Then("the account should have a valid IBAN")
    public void the_account_should_have_a_valid_iban() {
        assertNotNull(createdAccount);
        assertNotNull(createdAccount.getIban());
        assertTrue(createdAccount.getIban().length() > 0);
    }

    @Then("the account should have zero balance initially")
    public void the_account_should_have_zero_balance_initially() {
        assertNotNull(createdAccount);
        assertEquals(0.0, createdAccount.getBalance(), 0.01);
    }

    @Then("I should receive a list of {int} accounts")
    public void i_should_receive_a_list_of_accounts(int expectedCount) {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
        assertNotNull(lastResponse.getBody());
        accountList = (List<AccountDTO>) lastResponse.getBody();
        assertEquals(expectedCount, accountList.size());
    }

    @Then("each account should contain valid account information")
    public void each_account_should_contain_valid_account_information() {
        assertNotNull(accountList);
        for (AccountDTO account : accountList) {
            assertNotNull(account.getId());
            assertNotNull(account.getIban());
            assertNotNull(account.getTypeOfAccount());
            assertTrue(account.getBalance() >= 0);
        }
    }

    @Then("I should receive the account details")
    public void i_should_receive_the_account_details() {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
        assertNotNull(lastResponse.getBody());
        AccountDTO account = (AccountDTO) lastResponse.getBody();
        assertNotNull(account.getId());
    }

    @Then("the account should contain all required fields")
    public void the_account_should_contain_all_required_fields() {
        AccountDTO account = (AccountDTO) lastResponse.getBody();
        assertNotNull(account.getId());
        assertNotNull(account.getIban());
        assertNotNull(account.getTypeOfAccount());
        assertNotNull(account.getDateOfOpening());
    }

    @Then("all accounts should belong to that user")
    public void all_accounts_should_belong_to_that_user() {
        String realUserIdString = (String) testContext.testData.get("realUserId");
        UUID expectedUserId = UUID.fromString(realUserIdString);

        System.out.println("Checking accounts belong to user: " + expectedUserId);

        assertNotNull("Account list should not be null", accountList);
        for (AccountDTO account : accountList) {
            System.out.println("Account belongs to user: " + account.getUserId());
            assertEquals("Account should belong to the expected user",
                    expectedUserId, account.getUserId());
        }
    }

    @Then("the account should be deleted successfully")
    public void the_account_should_be_deleted_successfully() {
        String realAccountId = (String) testContext.testData.get("realAccountId");
        assertNotNull("Real account ID should not be null", realAccountId);
        assertEquals(HttpStatus.NO_CONTENT, lastResponse.getStatusCode());
    }

    @Then("the account should no longer exist")
    public void the_account_should_no_longer_exist() {
        String realAccountIdString = (String) testContext.testData.get("realAccountId");
        assertNotNull("Real account ID should not be null", realAccountIdString);

        UUID accountId = UUID.fromString(realAccountIdString);
        assertFalse("Account should no longer exist", testContext.accountRepository.existsById(accountId));
    }

    @Then("I should receive a {int} not found response")
    public void i_should_receive_a_not_found_response(int statusCode) {
        assertEquals(statusCode, lastResponse.getStatusCodeValue());
    }
}