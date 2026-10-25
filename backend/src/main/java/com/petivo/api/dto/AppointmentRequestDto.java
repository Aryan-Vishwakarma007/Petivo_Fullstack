package com.petivo.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class AppointmentRequestDto {

    @JsonAlias({"petName", "pet"})
    private String pet;

    private Long petId;

    @JsonAlias({"ownerName", "owner"})
    private String owner;

    @JsonAlias({"serviceName", "service"})
    private String service;

    private Long serviceId;

    @JsonAlias({"appointmentDate", "date"})
    private String date;

    private String status = "Pending";

    private String notes;

    private String petType;

    public AppointmentRequestDto() {
    }

    public String getPet() {
        return pet;
    }

    public void setPet(String pet) {
        this.pet = pet;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }
}
