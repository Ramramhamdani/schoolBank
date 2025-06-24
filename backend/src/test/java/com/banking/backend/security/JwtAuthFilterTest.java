package com.banking.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.ServletException;
import java.io.IOException;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthFilterTest {

    private JwtUtil jwtUtil;
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setup() {
        jwtUtil = mock(JwtUtil.class);
        jwtAuthFilter = new JwtAuthFilter(jwtUtil);  // inject mock here
    }

    @Test
    void doFilter_internal_validToken_allowsRequest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = mock(MockFilterChain.class);

        // Set valid Authorization header
        request.addHeader("Authorization", "Bearer valid.jwt.token");

        // Mock JwtUtil behavior for valid token
        when(jwtUtil.validateToken("valid.jwt.token")).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Verify the filter chain proceeds
        verify(filterChain).doFilter(request, response);

        assertEquals(200, response.getStatus());
    }

    @Test
    void doFilter_internal_invalidToken_blocksRequest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = mock(MockFilterChain.class);

        // Set invalid Authorization header
        request.addHeader("Authorization", "Bearer invalid.jwt.token");

        // Mock JwtUtil behavior for invalid token
        when(jwtUtil.validateToken("invalid.jwt.token")).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Filter chain should NOT proceed
        verify(filterChain, never()).doFilter(request, response);

        assertEquals(401, response.getStatus());
    }
}