package com.banking.backend.cucumber.helpers;

import com.banking.backend.repository.UserRepository;
import com.banking.backend.repository.AccountRepository;
import com.banking.backend.service.UserService;
import com.banking.backend.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TestContext {

    @Autowired
    public TestRestTemplate restTemplate;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public UserService userService;

    @Autowired
    public AccountService accountService;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public AccountRepository accountRepository;

    public String jwtToken;
    public Map<String, Object> testData = new HashMap<>();

    public HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (jwtToken != null) {
            headers.setBearerAuth(jwtToken);
        }else {
            System.out.println("No JWT token available in createAuthHeaders()!"); // DEBUG
        }
        return headers;
    }
}