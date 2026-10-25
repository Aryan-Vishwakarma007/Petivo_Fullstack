package com.petivo.api.dto;

import com.petivo.api.entity.Pet;
import java.time.LocalDateTime;

public class PetDto {
    private Long id;
    private Long ownerId;
    private String ownerName;
    private String name;
    private String type;
    private String breed;
    private Integer age;
    private LocalDateTime createdAt;

    public PetDto() {
    }

    public PetDto(Long id, Long ownerId, String ownerName, String name, String type, String breed, Integer age, LocalDateTime createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.age = age;
        this.createdAt = createdAt;
    }

    public static PetDto fromEntity(Pet pet) {
        if (pet == null) return null;
        return new PetDto(
            pet.getId(),
            pet.getOwner() != null ? pet.getOwner().getId() : null,
            pet.getOwner() != null ? pet.getOwner().getName() : null,
            pet.getName(),
            pet.getType(),
            pet.getBreed(),
            pet.getAge(),
            pet.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
