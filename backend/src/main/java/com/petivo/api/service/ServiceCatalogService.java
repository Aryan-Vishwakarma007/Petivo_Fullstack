package com.petivo.api.service;

import com.petivo.api.dto.ServiceDto;
import com.petivo.api.entity.Service;
import com.petivo.api.exception.BadRequestException;
import com.petivo.api.exception.ResourceNotFoundException;
import com.petivo.api.repository.ServiceRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceCatalogService {

    private final ServiceRepository serviceRepository;

    public ServiceCatalogService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<ServiceDto> getAllServices(boolean activeOnly) {
        List<Service> services = activeOnly
                ? serviceRepository.findByActiveTrue()
                : serviceRepository.findAll();

        return services.stream().map(ServiceDto::fromEntity).collect(Collectors.toList());
    }

    public ServiceDto getServiceById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        return ServiceDto.fromEntity(service);
    }

    @Transactional
    public ServiceDto createService(ServiceDto dto) {
        if (serviceRepository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new BadRequestException("Service with name '" + dto.getName() + "' already exists");
        }

        Service service = new Service();
        service.setName(dto.getName().trim());
        service.setDescription(dto.getDescription());
        service.setPrice(dto.getPrice());
        service.setActive(dto.getActive() != null ? dto.getActive() : true);

        Service saved = serviceRepository.save(service);
        return ServiceDto.fromEntity(saved);
    }

    @Transactional
    public ServiceDto updateService(Long id, ServiceDto dto) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));

        service.setName(dto.getName().trim());
        service.setDescription(dto.getDescription());
        service.setPrice(dto.getPrice());
        if (dto.getActive() != null) {
            service.setActive(dto.getActive());
        }

        Service updated = serviceRepository.save(service);
        return ServiceDto.fromEntity(updated);
    }

    @Transactional
    public void deleteService(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        serviceRepository.delete(service);
    }
}
