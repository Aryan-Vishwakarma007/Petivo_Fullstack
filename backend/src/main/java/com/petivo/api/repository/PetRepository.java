package com.petivo.api.repository;

import com.petivo.api.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByOwnerId(Long ownerId);

    List<Pet> findByOwnerEmail(String email);

    Optional<Pet> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<Pet> findByNameIgnoreCaseAndOwnerId(String name, Long ownerId);
}
