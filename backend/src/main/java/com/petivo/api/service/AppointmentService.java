package com.petivo.api.service;

import com.petivo.api.dto.AdminStatsDto;
import com.petivo.api.dto.AppointmentDto;
import com.petivo.api.dto.AppointmentRequestDto;
import com.petivo.api.entity.*;
import com.petivo.api.exception.BadRequestException;
import com.petivo.api.exception.ResourceNotFoundException;
import com.petivo.api.repository.AppointmentRepository;
import com.petivo.api.repository.PetRepository;
import com.petivo.api.repository.ServiceRepository;
import com.petivo.api.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PetRepository petRepository,
                              ServiceRepository serviceRepository,
                              UserRepository userRepository,
                              AuthService authService) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public List<AppointmentDto> getAllAppointments() {
        User currentUser = authService.getCurrentAuthenticatedUser();

        List<Appointment> appointments;
        if (currentUser == null || currentUser.getRole() == Role.ADMIN) {
            appointments = appointmentRepository.findAll();
        } else {
            appointments = appointmentRepository.findForUser(currentUser.getId(), currentUser.getName());
        }

        return appointments.stream()
                .map(AppointmentDto::fromEntity)
                .collect(Collectors.toList());
    }

    public AppointmentDto getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser != null && currentUser.getRole() != Role.ADMIN) {
            if (appointment.getUser() != null && !appointment.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("You are not authorized to view this appointment");
            }
        }

        return AppointmentDto.fromEntity(appointment);
    }

    @Transactional
    public AppointmentDto createAppointment(AppointmentRequestDto dto) {
        User currentUser = authService.getCurrentAuthenticatedUser();

        // If not authenticated, fallback to demo user or find by provided owner
        if (currentUser == null) {
            if (dto.getOwner() != null && !dto.getOwner().isBlank()) {
                currentUser = userRepository.findByEmailOrNameIgnoreCase(dto.getOwner()).orElse(null);
            }
            if (currentUser == null) {
                currentUser = userRepository.findById(2L).orElseGet(() -> {
                    List<User> all = userRepository.findAll();
                    return all.isEmpty() ? null : all.get(0);
                });
            }
        }

        // Resolve Pet entity
        Pet pet = null;
        if (dto.getPetId() != null) {
            pet = petRepository.findById(dto.getPetId()).orElse(null);
        } else if (dto.getPet() != null && !dto.getPet().isBlank()) {
            String petName = dto.getPet().trim();
            if (currentUser != null) {
                Optional<Pet> existingPet = petRepository.findByNameIgnoreCaseAndOwnerId(petName, currentUser.getId());
                if (existingPet.isPresent()) {
                    pet = existingPet.get();
                } else {
                    // Automatically register the pet for this user
                    Pet newPet = new Pet();
                    newPet.setName(petName);
                    newPet.setType(dto.getPetType() != null && !dto.getPetType().isBlank() ? dto.getPetType() : "Dog");
                    newPet.setOwner(currentUser);
                    pet = petRepository.save(newPet);
                }
            }
        }

        // Resolve Service entity
        com.petivo.api.entity.Service service = null;
        if (dto.getServiceId() != null) {
            service = serviceRepository.findById(dto.getServiceId()).orElse(null);
        } else if (dto.getService() != null && !dto.getService().isBlank()) {
            String serviceName = dto.getService().trim();
            Optional<com.petivo.api.entity.Service> existingService = serviceRepository.findByNameIgnoreCase(serviceName);
            if (existingService.isPresent()) {
                service = existingService.get();
            } else {
                // Create service on the fly if needed
                com.petivo.api.entity.Service newService = new com.petivo.api.entity.Service();
                newService.setName(serviceName);
                newService.setPrice(new BigDecimal("499.00"));
                newService.setActive(true);
                newService.setDescription("Service booked via appointment portal");
                service = serviceRepository.save(newService);
            }
        }

        Appointment appointment = new Appointment();
        appointment.setUser(currentUser);
        appointment.setPetEntity(pet);
        appointment.setServiceEntity(service);

        // Populate both backward-compatible and normalized columns
        appointment.setPet(dto.getPet() != null && !dto.getPet().isBlank() ? dto.getPet() : (pet != null ? pet.getName() : "Pet"));
        appointment.setOwner(dto.getOwner() != null && !dto.getOwner().isBlank() ? dto.getOwner() : (currentUser != null ? currentUser.getName() : "Pet Owner"));
        appointment.setService(dto.getService() != null && !dto.getService().isBlank() ? dto.getService() : (service != null ? service.getName() : "General Service"));
        
        String appointmentDate = dto.getDate();
        if (appointmentDate == null || appointmentDate.isBlank()) {
            throw new BadRequestException("Appointment date is required");
        }
        appointment.setAppointmentDate(appointmentDate);
        appointment.setDate(appointmentDate);

        appointment.setStatus(dto.getStatus() != null && !dto.getStatus().isBlank() ? dto.getStatus() : "Pending");
        appointment.setNotes(dto.getNotes());

        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentDto.fromEntity(saved);
    }

    @Transactional
    public AppointmentDto updateAppointment(Long id, AppointmentRequestDto dto) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser != null && currentUser.getRole() != Role.ADMIN) {
            if (existing.getUser() != null && !existing.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("You are not authorized to update this appointment");
            }
        }

        if (dto.getPet() != null && !dto.getPet().isBlank()) {
            existing.setPet(dto.getPet().trim());
            if (existing.getPetEntity() != null) {
                existing.getPetEntity().setName(dto.getPet().trim());
            }
        }

        if (dto.getOwner() != null && !dto.getOwner().isBlank()) {
            existing.setOwner(dto.getOwner().trim());
        }

        if (dto.getService() != null && !dto.getService().isBlank()) {
            existing.setService(dto.getService().trim());
            Optional<com.petivo.api.entity.Service> serviceOpt = serviceRepository.findByNameIgnoreCase(dto.getService().trim());
            serviceOpt.ifPresent(existing::setServiceEntity);
        }

        if (dto.getDate() != null && !dto.getDate().isBlank()) {
            existing.setDate(dto.getDate().trim());
            existing.setAppointmentDate(dto.getDate().trim());
        }

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            existing.setStatus(dto.getStatus().trim());
        }

        if (dto.getNotes() != null) {
            existing.setNotes(dto.getNotes());
        }

        Appointment saved = appointmentRepository.save(existing);
        return AppointmentDto.fromEntity(saved);
    }

    @Transactional
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser != null && currentUser.getRole() != Role.ADMIN) {
            if (appointment.getUser() != null && !appointment.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("You are not authorized to delete this appointment");
            }
        }

        appointmentRepository.delete(appointment);
    }

    public AdminStatsDto getAdminStats() {
        long totalAppointments = appointmentRepository.count();
        long confirmed = appointmentRepository.countByStatusIgnoreCase("Confirmed");
        long pending = appointmentRepository.countByStatusIgnoreCase("Pending");
        long totalUsers = userRepository.count();
        long totalPets = petRepository.count();
        long totalServices = serviceRepository.count();

        return new AdminStatsDto(totalAppointments, confirmed, pending, totalUsers, totalPets, totalServices);
    }
}
