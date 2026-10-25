package com.petivo.api.service;

import com.petivo.api.dto.UserDto;
import com.petivo.api.entity.Role;
import com.petivo.api.entity.User;
import com.petivo.api.exception.BadRequestException;
import com.petivo.api.model.LoginRequest;
import com.petivo.api.model.LoginResponse;
import com.petivo.api.model.RegisterRequest;
import com.petivo.api.repository.UserRepository;
import com.petivo.api.security.JwtTokenProvider;
import com.petivo.api.security.UserPrincipal;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        // Security rule: regular registration always creates standard USER role
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        UserPrincipal principal = UserPrincipal.create(savedUser);
        String token = tokenProvider.generateToken(principal);

        return new LoginResponse(
                true,
                token,
                savedUser.getRole().name().toLowerCase(),
                "Registration successful",
                UserDto.fromEntity(savedUser)
        );
    }

    public LoginResponse login(LoginRequest request) {
        String usernameOrEmail = request.getUsername() != null ? request.getUsername().trim() : "";
        User user = userRepository.findByEmailOrNameIgnoreCase(usernameOrEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Validate requested role if provided
        if (request.getRole() != null && !request.getRole().isBlank()) {
            String expectedRole = request.getRole().trim().toUpperCase();
            if ("ADMIN".equals(expectedRole) && user.getRole() != Role.ADMIN) {
                throw new BadCredentialsException("User does not have administrator privileges");
            }
        }

        UserPrincipal principal = UserPrincipal.create(user);
        String token = tokenProvider.generateToken(principal);

        return new LoginResponse(
                true,
                token,
                user.getRole().name().toLowerCase(),
                "Login successful",
                UserDto.fromEntity(user)
        );
    }

    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        if (authentication.getPrincipal() instanceof UserPrincipal principal) {
            return userRepository.findById(principal.getId()).orElse(null);
        }

        String username = authentication.getName();
        return userRepository.findByEmailOrNameIgnoreCase(username).orElse(null);
    }
}
