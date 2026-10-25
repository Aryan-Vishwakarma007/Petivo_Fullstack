package com.petivo.api.dto;

import com.petivo.api.entity.Appointment;
import java.time.LocalDateTime;

public class AppointmentDto {
    private Long id;
    private String pet;
    private String owner;
    private String service;
    private String date;
    private String status;
    private Long petId;
    private Long serviceId;
    private Long userId;
    private String notes;
    private LocalDateTime createdAt;

    public AppointmentDto() {
    }

    public AppointmentDto(Long id, String pet, String owner, String service, String date, String status, Long petId, Long serviceId, Long userId, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.pet = pet;
        this.owner = owner;
        this.service = service;
        this.date = date;
        this.status = status;
        this.petId = petId;
        this.serviceId = serviceId;
        this.userId = userId;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static AppointmentDto fromEntity(Appointment appointment) {
        if (appointment == null) return null;
        return new AppointmentDto(
            appointment.getId(),
            appointment.getPet(),
            appointment.getOwner(),
            appointment.getService(),
            appointment.getDate(),
            appointment.getStatus(),
            appointment.getPetEntity() != null ? appointment.getPetEntity().getId() : null,
            appointment.getServiceEntity() != null ? appointment.getServiceEntity().getId() : null,
            appointment.getUser() != null ? appointment.getUser().getId() : null,
            appointment.getNotes(),
            appointment.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPet() {
        return pet;
    }

    public void setPet(String pet) {
        this.pet = pet;
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

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
