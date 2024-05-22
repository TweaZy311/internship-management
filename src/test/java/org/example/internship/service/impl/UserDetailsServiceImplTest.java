package org.example.internship.service.impl;

import org.example.internship.model.user.Role;
import org.example.internship.model.user.User;
import org.example.internship.repository.UserRepository;
import org.example.internship.service.user.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("test-user")
                .password("test-password")
                .role(Role.USER)
                .build();
    }

    @Test
    void loadUserByUsername_returnUserDetails() {
        when(userRepository.findByUsername("test-user")).thenReturn(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername("test-user");

        assertEquals("test-user", userDetails.getUsername());
        assertEquals("test-password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_userNotFound_throwException() {
        when(userRepository.findByUsername("non-existing-user")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("non-existing-user"));
    }
}