package com.petivo.api.config;

import com.petivo.api.entity.*;
import com.petivo.api.repository.ServiceRepository;
import com.petivo.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${petivo.bootstrap.admin-email:}")
    private String bootstrapAdminEmail;

    @Value("${petivo.bootstrap.admin-password:}")
    private String bootstrapAdminPassword;

    public DataInitializer(UserRepository userRepository,
                           ServiceRepository serviceRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        logger.info("Checking PETIVO initial database seed...");

        bootstrapAdministrator();
        seedService("Basic Bath", "A refreshing bath, gentle shampoo, and towel drying.", new BigDecimal("399.00"));
        seedService("Haircut & Styling", "A neat haircut and styling suited to your pet.", new BigDecimal("599.00"));
        seedService("Nail Trimming", "Careful nail trimming for your pet's comfort.", new BigDecimal("199.00"));
        seedService("Full Grooming", "Bath, brushing, haircut, nail care, and ear cleaning.", new BigDecimal("899.00"));
        seedService("Veterinary Checkup", "Comprehensive health examination by a licensed veterinarian.", new BigDecimal("799.00"));
        seedService("Bath & Blow Dry", "Full coat washing, hypoallergenic shampoo, and warm blow dry.", new BigDecimal("499.00"));
        logger.info("PETIVO initial database seed check completed.");
    }

    private void bootstrapAdministrator() {
        boolean hasEmail = StringUtils.hasText(bootstrapAdminEmail);
        boolean hasPassword = StringUtils.hasText(bootstrapAdminPassword);
        if (hasEmail != hasPassword) {
            throw new IllegalStateException(
                    "Set both PETIVO_ADMIN_EMAIL and PETIVO_ADMIN_PASSWORD to bootstrap an administrator.");
        }
        if (!hasEmail) {
            logger.info("Administrator bootstrap is disabled; no admin credentials were configured.");
            return;
        }

        String email = bootstrapAdminEmail.trim().toLowerCase();
        userRepository.findByEmail(email).ifPresentOrElse(existing -> {
            if (existing.getRole() != Role.ADMIN) {
                throw new IllegalStateException(
                        "The configured PETIVO_ADMIN_EMAIL belongs to a non-administrator account.");
            }
        }, () -> {
            User admin = new User();
            admin.setName("Administrator");
            admin.setEmail(email);
            admin.setPasswordHash(passwordEncoder.encode(bootstrapAdminPassword));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            logger.info("Configured administrator account created.");
        });
    }

    private void seedService(String name, String description, BigDecimal price) {
        if (!serviceRepository.existsByNameIgnoreCase(name)) {
            Service service = new Service(name, description, price, true);
            serviceRepository.save(service);
        }
    }

}
