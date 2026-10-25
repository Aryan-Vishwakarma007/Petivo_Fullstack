package com.petivo.api.repository;

import com.petivo.api.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUserId(Long userId);

    List<Appointment> findByUserEmail(String email);

    List<Appointment> findByPetEntityId(Long petId);

    Optional<Appointment> findByIdAndUserId(Long id, Long userId);

    long countByStatusIgnoreCase(String status);

    @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId OR (a.user IS NULL AND LOWER(a.owner) = LOWER(:ownerName))")
    List<Appointment> findForUser(@Param("userId") Long userId, @Param("ownerName") String ownerName);
}
