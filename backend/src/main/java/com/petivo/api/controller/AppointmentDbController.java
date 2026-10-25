package com.petivo.api.controller;

import com.petivo.api.dto.ApiResponse;
import com.petivo.api.dto.AppointmentDto;
import com.petivo.api.dto.AppointmentRequestDto;
import com.petivo.api.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/db/appointments", "/api/appointments"})
public class AppointmentDbController {

    private final AppointmentService appointmentService;

    public AppointmentDbController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // READ - all appointments visible to current user (or all if admin)
    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getAll() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    // READ - one appointment by ID
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    // CREATE - book an appointment
    @PostMapping
    public ResponseEntity<AppointmentDto> create(@RequestBody AppointmentRequestDto appointmentRequest) {
        AppointmentDto created = appointmentService.createAppointment(appointmentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // UPDATE - edit an appointment
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDto> update(
            @PathVariable Long id,
            @RequestBody AppointmentRequestDto updatedAppointment) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, updatedAppointment));
    }

    // DELETE - cancel an appointment
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok(new ApiResponse(true, "Appointment deleted successfully"));
    }
}
