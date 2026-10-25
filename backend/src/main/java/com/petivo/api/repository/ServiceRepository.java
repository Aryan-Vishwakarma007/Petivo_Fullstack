package com.petivo.api.repository;

import com.petivo.api.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByActiveTrue();

    Optional<Service> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
