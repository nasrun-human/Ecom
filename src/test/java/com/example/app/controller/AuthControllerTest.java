package com.example.app.controller;

import com.example.app.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.junit.jupiter.api.BeforeEach;

@SpringBootTest
@Transactional
public class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGetMe_WithValidCredentials_ReturnsUser() throws Exception {
        // Admin user is seeded by DataSeeder (admin/admin123)
        mockMvc.perform(get("/api/auth/me")
                .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    public void testGetMe_WithInvalidCredentials_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .with(httpBasic("admin", "wrongpassword")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRegister_WithValidData_ReturnsCreatedUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("test@test.com");
        request.setFullName("Test User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void testRegister_WithShortUsername_ReturnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ab"); // < 3 chars
        request.setPassword("password123");
        request.setEmail("test@test.com");
        request.setFullName("Test User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
