package com.banking.backend.controller;

import com.banking.backend.dto.ATMDTO;
import com.banking.backend.model.Account;
import com.banking.backend.model.AccountType;
import com.banking.backend.model.Transaction;
import com.banking.backend.model.TransactionType;
import com.banking.backend.model.User;
import com.banking.backend.service.AccountService;
import com.banking.backend.service.TransactionService;
import com.banking.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock private TransactionService transactionService;
    @Mock private AccountService accountService;
    @Mock private UserService userService;

    @InjectMocks private TransactionController transactionController;

    private ObjectMapper objectMapper;
    private User testUser;
    private Account fromAccount;
    private Account toAccount;
    private Transaction testTransaction;

    private UUID testTransactionId;
    private UUID testFromAccountId;
    private UUID testToAccountId;
    private LocalDateTime testTimestamp;

    @BeforeEach
    void setUp() {
        // 1) Generate unique IDs and a timestamp
        testTransactionId = UUID.randomUUID();
        testFromAccountId = UUID.randomUUID();
        testToAccountId   = UUID.randomUUID();
        testTimestamp     = LocalDateTime.now();

        // 2) Create a dummy User
        testUser = new User();
        UUID testUserId = UUID.randomUUID();
        testUser.setId(testUserId);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("user@example.com");

        // 3) Create the “from” Account
        fromAccount = new Account();
        fromAccount.setId(testFromAccountId);
        fromAccount.setIban("NL00TEST0123456789");
        fromAccount.setUser(testUser);
        fromAccount.setBalance(1000.0);
        fromAccount.setTypeOfAccount(AccountType.CURRENT);
        fromAccount.setDateOfOpening(LocalDate.now());
        fromAccount.setAbsoluteLimit(0.0);
        fromAccount.setActive(true);

        // 4) Create the “to” Account
        toAccount = new Account();
        toAccount.setId(testToAccountId);
        toAccount.setIban("NL00TEST9876543210");
        toAccount.setUser(testUser);
        toAccount.setBalance(500.0);
        toAccount.setTypeOfAccount(AccountType.SAVINGS);
        toAccount.setDateOfOpening(LocalDate.now());
        toAccount.setAbsoluteLimit(0.0);
        toAccount.setActive(true);

        // 5) Build a “successful” Transaction entity
        testTransaction = new Transaction();
        testTransaction.setId(testTransactionId);
        testTransaction.setFromAccount(fromAccount);
        testTransaction.setToAccount(toAccount);
        testTransaction.setAmount(500.00);
        testTransaction.setTypeOfTransaction(TransactionType.TRANSFER);
        testTransaction.setDateOfExecution(testTimestamp);
        testTransaction.setPerformingUser(testUser);
        testTransaction.setDescription("Test payment");

        // 6) Configure an ObjectMapper (ISO‐8601 for LocalDateTime)
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 7) Create a Jackson converter
        var jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);

        // 8) Build MockMvc WITHOUT springSecurity()
        mockMvc = MockMvcBuilders
                     .standaloneSetup(transactionController)
                     .setMessageConverters(
                         jacksonConverter,
                         new StringHttpMessageConverter() // for plain‐text error responses
                     )
                     .build();
    }

    @Test
    void createTransaction_Success() throws Exception {
        // Build JSON‐like request body matching TransactionDTO
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("fromIban",            fromAccount.getIban());
        requestBody.put("toIban",              toAccount.getIban());
        requestBody.put("amount",              testTransaction.getAmount());
        requestBody.put("typeOfTransaction",   testTransaction.getTypeOfTransaction().name());
        requestBody.put("dateOfExecution",     testTransaction.getDateOfExecution().toString());
        requestBody.put("performingUserId",    testTransaction.getPerformingUser().getId().toString());
        requestBody.put("description",         testTransaction.getDescription());

        // 1) Mock accountService.getAccountByIban(...) for both accounts
        when(accountService.getAccountByIban(eq(fromAccount.getIban())))
            .thenReturn(Optional.of(fromAccount));
        when(accountService.getAccountByIban(eq(toAccount.getIban())))
            .thenReturn(Optional.of(toAccount));

        // 2) Mock userService.getUserById(...) for performing user
        when(userService.getUserById(eq(testTransaction.getPerformingUser().getId().toString())))
            .thenReturn(testTransaction.getPerformingUser());

        // 3) Mock transactionService.makeTransaction(...) to return testTransaction
        when(transactionService.makeTransaction(any(Transaction.class)))
            .thenReturn(testTransaction);

        // Execute POST /transactions
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
               .andExpect(status().isCreated())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id").value(testTransaction.getId().toString()))
               .andExpect(jsonPath("$.fromIban").value(fromAccount.getIban()))
               .andExpect(jsonPath("$.toIban").value(toAccount.getIban()))
               .andExpect(jsonPath("$.amount").value(testTransaction.getAmount()))
               .andExpect(jsonPath("$.typeOfTransaction")
                              .value(testTransaction.getTypeOfTransaction().name()))
               .andExpect(jsonPath("$.dateOfExecution")
                              .value(testTransaction.getDateOfExecution().toString()))
               .andExpect(jsonPath("$.performingUserId")
                              .value(testTransaction.getPerformingUser().getId().toString()))
               .andExpect(jsonPath("$.description")
                              .value(testTransaction.getDescription()));
    }

    @Test
    void createTransaction_InvalidAmount_NegativeValue() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("fromIban",            fromAccount.getIban());
        requestBody.put("toIban",              toAccount.getIban());
        requestBody.put("amount",              -100.00);  // negative
        requestBody.put("typeOfTransaction",   "TRANSFER");
        requestBody.put("dateOfExecution",     testTimestamp.toString());
        requestBody.put("performingUserId",    testUser.getId().toString());
        requestBody.put("description",         "Should fail because amount is negative");

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
               .andExpect(status().isBadRequest())
               .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
               .andExpect(content().string("Transaction amount must be positive"));
    }

    @Test
    void createTransaction_InvalidAmount_ZeroValue() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("fromIban",            fromAccount.getIban());
        requestBody.put("toIban",              toAccount.getIban());
        requestBody.put("amount",              0.00);      // zero
        requestBody.put("typeOfTransaction",   "TRANSFER");
        requestBody.put("dateOfExecution",     testTimestamp.toString());
        requestBody.put("performingUserId",    testUser.getId().toString());
        requestBody.put("description",         "Should fail because amount is zero");

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
               .andExpect(status().isBadRequest())
               .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
               .andExpect(content().string("Transaction amount must be greater than zero"));
    }

    @Test
void createTransaction_InsufficientFunds() throws Exception {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("fromIban",            fromAccount.getIban());
    requestBody.put("toIban",              toAccount.getIban());
    requestBody.put("amount",              10000.00);  // too large
    requestBody.put("typeOfTransaction",   "TRANSFER");
    requestBody.put("dateOfExecution",     testTimestamp.toString());
    requestBody.put("performingUserId",    testUser.getId().toString());
    requestBody.put("description",         "Should fail because insufficient funds");

    // Stub both accounts so controller can look them up before checking balance
    when(accountService.getAccountByIban(eq(fromAccount.getIban())))
        .thenReturn(Optional.of(fromAccount));
    when(accountService.getAccountByIban(eq(toAccount.getIban())))
        .thenReturn(Optional.of(toAccount));
    // Stub the performing user lookup
    when(userService.getUserById(eq(testUser.getId().toString())))
        .thenReturn(testUser);
    // Stub transactionService to throw insufficient funds error
    when(transactionService.makeTransaction(any(Transaction.class)))
        .thenThrow(new IllegalArgumentException("Insufficient funds in account"));

    mockMvc.perform(post("/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
           .andExpect(status().isBadRequest())
           .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
           .andExpect(content().string("Insufficient funds in account"));
}



    @Test
    void createTransaction_SameAccount() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("fromIban",            fromAccount.getIban());
        requestBody.put("toIban",              fromAccount.getIban()); // same
        requestBody.put("amount",              500.00);
        requestBody.put("typeOfTransaction",   "TRANSFER");
        requestBody.put("dateOfExecution",     testTimestamp.toString());
        requestBody.put("performingUserId",    testUser.getId().toString());
        requestBody.put("description",         "Should fail because same account");

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
               .andExpect(status().isBadRequest())
               .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
               .andExpect(content().string("Cannot transfer to the same account"));
    }

    @Test
    void createTransaction_AccountNotFound() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("fromIban",            "INVALID_IBAN");
        requestBody.put("toIban",              toAccount.getIban());
        requestBody.put("amount",              500.00);
        requestBody.put("typeOfTransaction",   "TRANSFER");
        requestBody.put("dateOfExecution",     testTimestamp.toString());
        requestBody.put("performingUserId",    testUser.getId().toString());
        requestBody.put("description",         "Should fail because account not found");

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
               .andExpect(status().isBadRequest())
               .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
               .andExpect(content().string("Source account not found"));
    }

    @Test
    void createTransaction_EmptyRequestBody() throws Exception {
        String emptyJson = "{}";

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyJson))
               .andExpect(status().isBadRequest());
    }

    @Test
    void createTransaction_InvalidJson() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
               .andExpect(status().isBadRequest());
    }

    @Test
    void getAccountTransactions_Success() throws Exception {
        // Prepare two extra transactions
        Account otherAccount = new Account();
        otherAccount.setId(UUID.randomUUID());
        otherAccount.setIban("NL00TESTOTHER123456");

        Transaction transaction2 = new Transaction();
        transaction2.setId(UUID.randomUUID());
        transaction2.setFromAccount(fromAccount);
        transaction2.setToAccount(otherAccount);
        transaction2.setAmount(200.00);
        transaction2.setTypeOfTransaction(TransactionType.TRANSFER);
        transaction2.setDateOfExecution(LocalDateTime.now().minusHours(1));
        transaction2.setPerformingUser(testUser);

        Transaction transaction3 = new Transaction();
        transaction3.setId(UUID.randomUUID());
        transaction3.setFromAccount(otherAccount);
        transaction3.setToAccount(fromAccount);
        transaction3.setAmount(300.00);
        transaction3.setTypeOfTransaction(TransactionType.TRANSFER);
        transaction3.setDateOfExecution(LocalDateTime.now().minusHours(2));
        transaction3.setPerformingUser(testUser);

        List<Transaction> transactions = Arrays.asList(testTransaction, transaction2, transaction3);
        when(transactionService.getTransactionsForAccount(eq(fromAccount.getId())))
            .thenReturn(transactions);

        // GET /accounts/{accountId}/transactions
        mockMvc.perform(get("/accounts/{accountId}/transactions", fromAccount.getId()))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(3))
               .andExpect(jsonPath("$[0].id").value(testTransaction.getId().toString()))
               .andExpect(jsonPath("$[0].fromIban").value(fromAccount.getIban()))
               .andExpect(jsonPath("$[0].toIban").value(toAccount.getIban()))
               .andExpect(jsonPath("$[0].amount").value(testTransaction.getAmount()))
               .andExpect(jsonPath("$[1].id").value(transaction2.getId().toString()))
               .andExpect(jsonPath("$[1].fromIban").value(fromAccount.getIban()))
               .andExpect(jsonPath("$[1].amount").value(transaction2.getAmount()))
               .andExpect(jsonPath("$[2].id").value(transaction3.getId().toString()))
               .andExpect(jsonPath("$[2].toIban").value(fromAccount.getIban()))
               .andExpect(jsonPath("$[2].amount").value(transaction3.getAmount()));
    }

    @Test
    void getAccountTransactions_EmptyList() throws Exception {
        when(transactionService.getTransactionsForAccount(eq(fromAccount.getId())))
            .thenReturn(Arrays.asList());

        mockMvc.perform(get("/accounts/{accountId}/transactions", fromAccount.getId()))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAccountTransactions_SingleTransaction() throws Exception {
        List<Transaction> singleTransaction = Arrays.asList(testTransaction);
        when(transactionService.getTransactionsForAccount(eq(fromAccount.getId())))
            .thenReturn(singleTransaction);

        mockMvc.perform(get("/accounts/{accountId}/transactions", fromAccount.getId()))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(1))
               .andExpect(jsonPath("$[0].id").value(testTransaction.getId().toString()))
               .andExpect(jsonPath("$[0].fromIban").value(fromAccount.getIban()))
               .andExpect(jsonPath("$[0].toIban").value(toAccount.getIban()))
               .andExpect(jsonPath("$[0].amount").value(testTransaction.getAmount()));
    }

    @Test
    void getAccountTransactions_InvalidUuid() throws Exception {
        mockMvc.perform(get("/accounts/{accountId}/transactions", "invalid-uuid"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void getAccountTransactions_NonExistentAccount() throws Exception {
        UUID nonExistentAccountId = UUID.randomUUID();
        when(transactionService.getTransactionsForAccount(eq(nonExistentAccountId)))
            .thenReturn(Arrays.asList());

        mockMvc.perform(get("/accounts/{accountId}/transactions", nonExistentAccountId))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(0));
    }
    @Test
    void deposit_Success() throws Exception {
        ATMDTO dto = new ATMDTO();
        dto.setIBAN("NL01BANK0123456789");
        dto.setAmount(200.0);

        Transaction mockTx = new Transaction();
        mockTx.setAmount(200.0);

        when(transactionService.createDeposit(any(ATMDTO.class))).thenReturn(mockTx);

        mockMvc.perform(post("/atm/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(200.0));
    }

    @Test
    void deposit_InvalidAmount_ShouldReturnBadRequest() throws Exception {
        ATMDTO dto = new ATMDTO();
        dto.setIBAN("NL01BANK0123456789");
        dto.setAmount(0.0);

        when(transactionService.createDeposit(any(ATMDTO.class)))
                .thenThrow(new IllegalArgumentException("Deposit amount must be greater than zero"));

        mockMvc.perform(post("/atm/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Deposit amount must be greater than zero"));
    }

    @Test
    void withdraw_Success() throws Exception {
        ATMDTO dto = new ATMDTO();
        dto.setIBAN("NL01BANK0123456789");
        dto.setAmount(150.0);

        Transaction mockTx = new Transaction();
        mockTx.setAmount(150.0);

        when(transactionService.createWithdrawal(any(ATMDTO.class))).thenReturn(mockTx);

        mockMvc.perform(post("/atm/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(150.0));
    }

    @Test
    void withdraw_InsufficientFunds_ShouldReturnBadRequest() throws Exception {
        ATMDTO dto = new ATMDTO();
        dto.setIBAN("NL01BANK0123456789");
        dto.setAmount(5000.0);

        when(transactionService.createWithdrawal(any(ATMDTO.class)))
                .thenThrow(new IllegalArgumentException("Insufficient funds in account"));

        mockMvc.perform(post("/atm/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient funds in account"));
    }
    @Test
    void getTransactionsReceivedByAccount_Success() throws Exception {
        Transaction tx = new Transaction();
        tx.setId(UUID.randomUUID());
        tx.setAmount(100.0);
        tx.setFromAccount(fromAccount);
        tx.setToAccount(toAccount);

        when(transactionService.getTransactionsByToAccount(toAccount.getId()))
                .thenReturn(List.of(tx));

        mockMvc.perform(get("/accounts/{accountId}/received", toAccount.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].amount").value(100.0));
    }

    @Test
    void getTransactionsReceivedByAccount_InvalidId_ShouldReturnBadRequest() throws Exception {
        UUID invalidId = UUID.randomUUID();
        when(transactionService.getTransactionsByToAccount(invalidId))
                .thenThrow(new IllegalArgumentException("Invalid account ID"));

        mockMvc.perform(get("/accounts/{accountId}/received", invalidId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid account ID"));
    }



}
