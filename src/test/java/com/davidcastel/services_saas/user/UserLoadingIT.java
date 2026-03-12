package com.davidcastel.services_saas.user;

import com.davidcastel.services_saas.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserDetailsServiceImpl service;

    @Test
    void load_user_by_username_ok() {
        var user = new com.davidcastel.services_saas.user.User(
                "john@test.com",
                "$2a$10$hash",
                Role.ADMIN
        );

        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("john@test.com");

        assertEquals("john@test.com", details.getUsername());
        assertEquals("$2a$10$hash", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(details.isEnabled());
    }

    @Test
    void load_user_by_username_not_found() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("john@test.com"));
    }
}