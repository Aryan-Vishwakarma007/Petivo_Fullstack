package com.petivo.api.service;

import com.petivo.api.dto.UserDto;
import com.petivo.api.dto.UserProfileUpdateDto;
import com.petivo.api.entity.User;
import com.petivo.api.exception.BadRequestException;
import com.petivo.api.exception.ResourceNotFoundException;
import com.petivo.api.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public UserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public UserDto getCurrentUserProfile() {
        User user = authService.getCurrentAuthenticatedUser();
        if (user == null) {
            throw new AccessDeniedException("User must be authenticated to view profile");
        }
        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto updateCurrentUserProfile(UserProfileUpdateDto dto) {
        User user = authService.getCurrentAuthenticatedUser();
        if (user == null) {
            throw new AccessDeniedException("User must be authenticated to update profile");
        }

        String newEmail = dto.getEmail().trim().toLowerCase();
        if (!user.getEmail().equalsIgnoreCase(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new BadRequestException("Email is already in use by another account");
        }

        user.setName(dto.getName().trim());
        user.setEmail(newEmail);

        User saved = userRepository.save(user);
        return UserDto.fromEntity(saved);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserDto.fromEntity(user);
    }
}
