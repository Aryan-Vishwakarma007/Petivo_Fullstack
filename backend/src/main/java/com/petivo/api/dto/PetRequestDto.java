package com.petivo.api.dto;

import jakarta.validation.constraints.NotBlank;

public class PetRequestDto {

    @NotBlank(message = "Pet name is required")
    private String name;

    @NotBlank(message = "Pet type is required (e.g., Dog, Cat)")
    private String type;

    private String breed;

    private Integer age;

    public PetRequestDto() {
    }

    public PetRequestDto(String name, String type, String breed, Integer age) {
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.age = age;
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
}
