package org.example.internship.controller;

import org.example.internship.InternshipApplicationTests;
import org.example.internship.entity.UserEntity;
import org.example.internship.entity.UserRole;
import org.example.internship.model.request.GenerateTokenRequest;
import org.example.internship.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

class AuthControllerTest extends InternshipApplicationTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setupTestUser() {
        userRepository.deleteAll();
        UserEntity user = UserEntity.builder()
                .email("test@example.org")
                .username("test_user")
                .password(passwordEncoder.encode("password123"))
                .name("test_user")
                .role(UserRole.USER)
                .build();
        userRepository.save(user);
    }

    @Test
    void shouldAuthenticateAndReturnTokens() throws Exception {
        GenerateTokenRequest request = new GenerateTokenRequest();
        request.setUsername("test_user");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void shouldRejectInvalidCredentials() throws Exception {
        GenerateTokenRequest request = new GenerateTokenRequest();
        request.setUsername("wrong_user");
        request.setPassword("wrong_pass");

        mockMvc.perform(post("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
