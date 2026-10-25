package com.petivo.api.controller;

import com.petivo.api.dto.ApiResponse;
import com.petivo.api.dto.PetDto;
import com.petivo.api.dto.PetRequestDto;
import com.petivo.api.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public ResponseEntity<List<PetDto>> getPets() {
        return ResponseEntity.ok(petService.getUserPets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDto> getPetById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.getPetById(id));
    }

    @PostMapping
    public ResponseEntity<PetDto> createPet(@Valid @RequestBody PetRequestDto dto) {
        PetDto created = petService.createPet(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetDto> updatePet(@PathVariable Long id, @Valid @RequestBody PetRequestDto dto) {
        return ResponseEntity.ok(petService.updatePet(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.ok(new ApiResponse(true, "Pet deleted successfully"));
    }
}
