package com.petivo.api.repository;

import com.petivo.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:identifier) OR LOWER(u.name) = LOWER(:identifier)")
    Optional<User> findByEmailOrNameIgnoreCase(@Param("identifier") String identifier);

    boolean existsByEmail(String email);
}
