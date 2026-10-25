package com.petivo.api;

import com.petivo.api.dto.AppointmentDto;
import com.petivo.api.dto.AppointmentRequestDto;
import com.petivo.api.repository.AppointmentRepository;
import com.petivo.api.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AppointmentServiceTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Test
    void testCreateAndRetrieveAppointment() {
        AppointmentRequestDto request = new AppointmentRequestDto();
        request.setPet("Rocky");
        request.setOwner("Shreyash");
        request.setService("Full Grooming");
        request.setDate("2026-11-05");
        request.setStatus("Pending");
        request.setNotes("First haircut");

        AppointmentDto created = appointmentService.createAppointment(request);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Rocky", created.getPet());
        assertEquals("Full Grooming", created.getService());
        assertEquals("2026-11-05", created.getDate());

        AppointmentDto retrieved = appointmentService.getAppointmentById(created.getId());
        assertNotNull(retrieved);
        assertEquals("Rocky", retrieved.getPet());
        assertEquals("Full Grooming", retrieved.getService());
    }

    @Test
    void testUpdateAppointment() {
        AppointmentRequestDto request = new AppointmentRequestDto();
        request.setPet("Daisy");
        request.setOwner("Shreyash");
        request.setService("Basic Bath");
        request.setDate("2026-11-10");
        request.setStatus("Pending");

        AppointmentDto created = appointmentService.createAppointment(request);

        AppointmentRequestDto updateRequest = new AppointmentRequestDto();
        updateRequest.setStatus("Confirmed");
        updateRequest.setDate("2026-11-12");

        AppointmentDto updated = appointmentService.updateAppointment(created.getId(), updateRequest);
        assertEquals("Confirmed", updated.getStatus());
        assertEquals("2026-11-12", updated.getDate());
    }

    @Test
    void testDeleteAppointment() {
        AppointmentRequestDto request = new AppointmentRequestDto();
        request.setPet("Charlie");
        request.setOwner("Shreyash");
        request.setService("Nail Trimming");
        request.setDate("2026-11-15");
        request.setStatus("Pending");

        AppointmentDto created = appointmentService.createAppointment(request);
        Long id = created.getId();
        assertTrue(appointmentRepository.existsById(id));

        appointmentService.deleteAppointment(id);
        assertFalse(appointmentRepository.existsById(id));
    }
}
