package com.petivo.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    @JsonIgnore
    private Pet petEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    @JsonIgnore
    private Service serviceEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "appointment_date")
    private String appointmentDate;

    @Column(nullable = false)
    private String status = "Pending";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Backward-compatible columns matching earlier schema and UI expectations
    @Column(name = "pet")
    private String pet;

    @Column(name = "owner")
    private String owner;

    @Column(name = "service")
    private String service;

    @Column(name = "date")
    private String date;

    @PrePersist
    @PreUpdate
    protected void syncFields() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (petEntity != null && (pet == null || pet.isBlank())) {
            pet = petEntity.getName();
        }
        if (serviceEntity != null && (service == null || service.isBlank())) {
            service = serviceEntity.getName();
        }
        if (user != null && (owner == null || owner.isBlank())) {
            owner = user.getName();
        }
        if (appointmentDate != null && (date == null || date.isBlank())) {
            date = appointmentDate;
        } else if (date != null && appointmentDate == null) {
            appointmentDate = date;
        }
        if (status == null || status.isBlank()) {
            status = "Pending";
        }
    }

    public Appointment() {
    }

    public Appointment(Long id, String pet, String owner, String service, String date, String status) {
        this.id = id;
        this.pet = pet;
        this.owner = owner;
        this.service = service;
        this.date = date;
        this.appointmentDate = date;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pet getPetEntity() {
        return petEntity;
    }

    public void setPetEntity(Pet petEntity) {
        this.petEntity = petEntity;
        if (petEntity != null) {
            this.pet = petEntity.getName();
        }
    }

    public Service getServiceEntity() {
        return serviceEntity;
    }

    public void setServiceEntity(Service serviceEntity) {
        this.serviceEntity = serviceEntity;
        if (serviceEntity != null) {
            this.service = serviceEntity.getName();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.owner = user.getName();
        }
    }

    public String getAppointmentDate() {
        return appointmentDate != null ? appointmentDate : date;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
        if (this.date == null) {
            this.date = appointmentDate;
        }
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPet() {
        if (pet != null && !pet.isBlank()) return pet;
        return petEntity != null ? petEntity.getName() : null;
    }

    public void setPet(String pet) {
        this.pet = pet;
    }

    public String getOwner() {
        if (owner != null && !owner.isBlank()) return owner;
        return user != null ? user.getName() : null;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getService() {
        if (service != null && !service.isBlank()) return service;
        return serviceEntity != null ? serviceEntity.getName() : null;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDate() {
        if (date != null && !date.isBlank()) return date;
        return appointmentDate;
    }

    public void setDate(String date) {
        this.date = date;
        if (this.appointmentDate == null) {
            this.appointmentDate = date;
        }
    }
}
