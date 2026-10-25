-- ============================================================================
-- PETIVO - Pet Care Management System Database Schema (MySQL 8)
-- ============================================================================

CREATE DATABASE IF NOT EXISTS petivo_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE petivo_db;

-- ----------------------------------------------------------------------------
-- 1. USERS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 2. PETS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    breed VARCHAR(100),
    age INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pets_owner FOREIGN KEY (owner_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_pet_owner (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 3. SERVICES TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    INDEX idx_service_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 4. APPOINTMENTS TABLE
-- Normalized schema with foreign keys, plus compatibility columns for legacy UI
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT,
    service_id BIGINT,
    user_id BIGINT,
    appointment_date VARCHAR(50),
    status VARCHAR(50) NOT NULL DEFAULT 'Pending',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Compatibility columns matching original UI/JPA fields
    pet VARCHAR(100),
    owner VARCHAR(100),
    service VARCHAR(100),
    date VARCHAR(50),
    CONSTRAINT fk_appointments_pet FOREIGN KEY (pet_id) 
        REFERENCES pets(id) ON DELETE SET NULL,
    CONSTRAINT fk_appointments_service FOREIGN KEY (service_id) 
        REFERENCES services(id) ON DELETE SET NULL,
    CONSTRAINT fk_appointments_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_appointment_user (user_id),
    INDEX idx_appointment_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- Idempotent public service catalog seed data
-- (Re-running this script will NOT duplicate or corrupt records)
-- ============================================================================

INSERT INTO services (name, description, price, active)
VALUES 
    ('Basic Bath', 'A refreshing bath, gentle shampoo, and towel drying.', 399.00, TRUE),
    ('Haircut & Styling', 'A neat haircut and styling suited to your pet.', 599.00, TRUE),
    ('Nail Trimming', 'Careful nail trimming for your pet''s comfort.', 199.00, TRUE),
    ('Full Grooming', 'Bath, brushing, haircut, nail care, and ear cleaning.', 899.00, TRUE),
    ('Veterinary Checkup', 'Comprehensive health examination by a licensed veterinarian.', 799.00, TRUE),
    ('Bath & Blow Dry', 'Full coat washing, hypoallergenic shampoo, and warm blow dry.', 499.00, TRUE)
ON DUPLICATE KEY UPDATE 
    description = VALUES(description),
    price = VALUES(price),
    active = VALUES(active);
