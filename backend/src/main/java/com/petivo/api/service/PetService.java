package com.petivo.api.service;

import com.petivo.api.dto.PetDto;
import com.petivo.api.dto.PetRequestDto;
import com.petivo.api.entity.Pet;
import com.petivo.api.entity.Role;
import com.petivo.api.entity.User;
import com.petivo.api.exception.BadRequestException;
import com.petivo.api.exception.ResourceNotFoundException;
import com.petivo.api.repository.PetRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final AuthService authService;

    public PetService(PetRepository petRepository, AuthService authService) {
        this.petRepository = petRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public List<PetDto> getUserPets() {
        User user = authService.getCurrentAuthenticatedUser();
        if (user == null) {
            throw new AccessDeniedException("User must be authenticated to view pets");
        }

        List<Pet> pets;
        if (user.getRole() == Role.ADMIN) {
            pets = petRepository.findAll();
        } else {
            pets = petRepository.findByOwnerId(user.getId());
        }

        return pets.stream().map(PetDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PetDto getPetById(Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        if (user == null) {
            throw new AccessDeniedException("User must be authenticated to view pet");
        }

        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        if (user.getRole() != Role.ADMIN && !pet.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to view this pet");
        }

        return PetDto.fromEntity(pet);
    }

    @Transactional
    public PetDto createPet(PetRequestDto dto) {
        User user = authService.getCurrentAuthenticatedUser();
        if (user == null) {
            throw new AccessDeniedException("User must be authenticated to register a pet");
        }

        Pet pet = new Pet();
        pet.setOwner(user);
        pet.setName(dto.getName().trim());
        pet.setType(dto.getType().trim());
        pet.setBreed(dto.getBreed() != null ? dto.getBreed().trim() : null);
        pet.setAge(dto.getAge());

        Pet saved = petRepository.save(pet);
        return PetDto.fromEntity(saved);
    }

    @Transactional
    public PetDto updatePet(Long id, PetRequestDto dto) {
        User user = authService.getCurrentAuthenticatedUser();
        if (user == null) {
            throw new AccessDeniedException("User must be authenticated to update a pet");
        }

        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        if (user.getRole() != Role.ADMIN && !pet.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to edit this pet");
        }

        pet.setName(dto.getName().trim());
        pet.setType(dto.getType().trim());
        pet.setBreed(dto.getBreed() != null ? dto.getBreed().trim() : null);
        pet.setAge(dto.getAge());

        Pet updated = petRepository.save(pet);
        return PetDto.fromEntity(updated);
    }

    @Transactional
    public void deletePet(Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        if (user == null) {
            throw new AccessDeniedException("User must be authenticated to delete a pet");
        }

        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        if (user.getRole() != Role.ADMIN && !pet.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this pet");
        }

        // Prevent orphaned appointment records by clearing pet foreign keys on appointments
        if (pet.getAppointments() != null) {
            pet.getAppointments().forEach(apt -> apt.setPetEntity(null));
        }

        petRepository.delete(pet);
    }
}
