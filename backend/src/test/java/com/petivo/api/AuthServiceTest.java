package com.petivo.api;

import com.petivo.api.entity.Role;
import com.petivo.api.entity.User;
import com.petivo.api.exception.BadRequestException;
import com.petivo.api.model.LoginRequest;
import com.petivo.api.model.LoginResponse;
import com.petivo.api.model.RegisterRequest;
import com.petivo.api.repository.UserRepository;
import com.petivo.api.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testRegisterNewUser() {
        RegisterRequest request = new RegisterRequest("Test User", "unique_test@example.com", "secretPassword123");
        LoginResponse response = authService.register(request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getToken());
        assertEquals("user", response.getRole());

        User saved = userRepository.findByEmail("unique_test@example.com").orElse(null);
        assertNotNull(saved);
        assertEquals("Test User", saved.getName());
        assertEquals(Role.USER, saved.getRole());
        // Verify password is not saved in plain text
        assertNotEquals("secretPassword123", saved.getPasswordHash());
    }

    @Test
    void testRegisterDuplicateEmailRejection() {
        RegisterRequest request1 = new RegisterRequest("User One", "duplicate@example.com", "password123");
        authService.register(request1);

        RegisterRequest request2 = new RegisterRequest("User Two", "duplicate@example.com", "password456");
        assertThrows(BadRequestException.class, () -> authService.register(request2));
    }

    @Test
    void testLoginValidCredentials() {
        RegisterRequest registerReq = new RegisterRequest("John Doe", "john@example.com", "mypassword123");
        authService.register(registerReq);

        LoginRequest loginReq = new LoginRequest("user", "john@example.com", "mypassword123");
        LoginResponse loginRes = authService.login(loginReq);

        assertTrue(loginRes.isSuccess());
        assertNotNull(loginRes.getToken());
        assertEquals("john@example.com", loginRes.getUser().getEmail());
    }

    @Test
    void testLoginInvalidPasswordRejection() {
        RegisterRequest registerReq = new RegisterRequest("Jane Doe", "jane@example.com", "correctPassword123");
        authService.register(registerReq);

        LoginRequest loginReq = new LoginRequest("user", "jane@example.com", "wrongPassword");
        assertThrows(BadCredentialsException.class, () -> authService.login(loginReq));
    }
}
