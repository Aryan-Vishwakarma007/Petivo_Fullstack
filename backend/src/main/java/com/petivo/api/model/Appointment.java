package com.petivo.api.model;

/**
 * Backward compatibility alias for com.petivo.api.entity.Appointment.
 * The primary JPA entity is located at com.petivo.api.entity.Appointment.
 */
public class Appointment extends com.petivo.api.entity.Appointment {
    public Appointment() {
        super();
    }

    public Appointment(Long id, String pet, String owner, String service, String date, String status) {
        super(id, pet, owner, service, date, status);
    }
}
