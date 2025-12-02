-- Health Track Personal Wellness Platform
-- Database Schema Creation Script
-- Group 27

-- Drop existing tables if they exist (in reverse dependency order)
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS MONTHLY_SUMMARY;
DROP TABLE IF EXISTS GROUP_MEMBER;
DROP TABLE IF EXISTS FAMILY_GROUP;
DROP TABLE IF EXISTS INVITATION;
DROP TABLE IF EXISTS CHALLENGE_PARTICIPANT;
DROP TABLE IF EXISTS CHALLENGE;
DROP TABLE IF EXISTS APPOINTMENT;
DROP TABLE IF EXISTS ACTION;
DROP TABLE IF EXISTS USER_PROVIDER;
DROP TABLE IF EXISTS PHONE;
DROP TABLE IF EXISTS EMAIL;
DROP TABLE IF EXISTS PROVIDER;
DROP TABLE IF EXISTS USER;

SET FOREIGN_KEY_CHECKS = 1;

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS healthtrack CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE healthtrack;

-- 1. USER Table
CREATE TABLE USER (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    health_id VARCHAR(50) NOT NULL UNIQUE COMMENT 'Candidate key',
    full_name VARCHAR(100) NOT NULL,
    account_status ENUM('Active', 'Inactive', 'Suspended') DEFAULT 'Active',
    user_role ENUM('Patient', 'Provider', 'Caregiver', 'Admin') DEFAULT 'Patient' COMMENT 'User role in the system',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_health_id (health_id),
    INDEX idx_user_role (user_role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. EMAIL Table (Multi-valued attribute)
CREATE TABLE EMAIL (
    email VARCHAR(255) PRIMARY KEY,
    user_id INT NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    verified_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES USER(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_email (email),
    CONSTRAINT chk_email_verification CHECK (
        (is_verified = TRUE AND verified_at IS NOT NULL) OR 
        (is_verified = FALSE AND verified_at IS NULL)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. PHONE Table (0..1 relationship with USER)
CREATE TABLE PHONE (
    phone_number VARCHAR(20) PRIMARY KEY,
    user_id INT NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    verified_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES USER(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_phone (user_id),
    INDEX idx_user_id (user_id),
    CONSTRAINT chk_phone_verification CHECK (
        (is_verified = TRUE AND verified_at IS NOT NULL) OR 
        (is_verified = FALSE AND verified_at IS NULL)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. PROVIDER Table
CREATE TABLE PROVIDER (
    provider_id INT AUTO_INCREMENT PRIMARY KEY,
    license_no VARCHAR(50) NOT NULL UNIQUE COMMENT 'Candidate key',
    provider_name VARCHAR(100) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    verified_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_license_no (license_no),
    CONSTRAINT chk_provider_verification CHECK (
        (is_verified = TRUE AND verified_at IS NOT NULL) OR 
        (is_verified = FALSE AND verified_at IS NULL)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. USER_PROVIDER Table (M:N relationship with primary provider constraint)
CREATE TABLE USER_PROVIDER (
    user_provider_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    provider_id INT NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    link_status ENUM('Active', 'Inactive') DEFAULT 'Active',
    linked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    unlinked_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES USER(user_id) ON DELETE CASCADE,
    FOREIGN KEY (provider_id) REFERENCES PROVIDER(provider_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_provider (user_id, provider_id),
    INDEX idx_user_id (user_id),
    INDEX idx_provider_id (provider_id),
    INDEX idx_is_primary (is_primary)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. ACTION Table (Superclass)
CREATE TABLE ACTION (
    action_id INT AUTO_INCREMENT PRIMARY KEY,
    action_type ENUM('Appointment', 'Challenge') NOT NULL,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES USER(user_id) ON DELETE CASCADE,
    INDEX idx_action_type (action_type),
    INDEX idx_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. APPOINTMENT Table (Subclass of ACTION)
CREATE TABLE APPOINTMENT (
    action_id INT PRIMARY KEY,
    provider_id INT NOT NULL,
    scheduled_at DATETIME NOT NULL,
    consultation_type ENUM('InPerson', 'Virtual') NOT NULL,
    memo TEXT,
    status ENUM('Scheduled', 'Cancelled') DEFAULT 'Scheduled',
    cancel_reason TEXT,
    cancel_time TIMESTAMP NULL,
    FOREIGN KEY (action_id) REFERENCES ACTION(action_id) ON DELETE CASCADE,
    FOREIGN KEY (provider_id) REFERENCES PROVIDER(provider_id) ON DELETE CASCADE,
    INDEX idx_provider_id (provider_id),
    INDEX idx_scheduled_at (scheduled_at),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. CHALLENGE Table (Subclass of ACTION)
CREATE TABLE CHALLENGE (
    action_id INT PRIMARY KEY,
    goal VARCHAR(500) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('Draft', 'Active', 'Completed', 'Cancelled', 'Expired') DEFAULT 'Draft',
    FOREIGN KEY (action_id) REFERENCES ACTION(action_id) ON DELETE CASCADE,
    INDEX idx_status (status),
    INDEX idx_dates (start_date, end_date),
    CONSTRAINT chk_challenge_dates CHECK (end_date >= start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. CHALLENGE_PARTICIPANT Table (M:N relationship)
CREATE TABLE CHALLENGE_PARTICIPANT (
    challenge_participant_id INT AUTO_INCREMENT PRIMARY KEY,
    action_id INT NOT NULL,
    user_id INT NOT NULL,
    progress_value DECIMAL(10, 2) DEFAULT 0,
    progress_unit VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    participant_status ENUM('Invited', 'Joined', 'Declined', 'Removed') DEFAULT 'Invited',
    FOREIGN KEY (action_id) REFERENCES CHALLENGE(action_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USER(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_challenge_user (action_id, user_id),
    INDEX idx_action_id (action_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (participant_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. INVITATION Table
CREATE TABLE INVITATION (
    invitation_id INT AUTO_INCREMENT PRIMARY KEY,
    invitation_type ENUM('Challenge', 'DataShare') NOT NULL,
    target_email VARCHAR(255) NULL,
    target_phone VARCHAR(20) NULL,
    initiated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    status ENUM('Pending', 'Accepted', 'Expired', 'Cancelled') DEFAULT 'Pending',
    to_new_user BOOLEAN DEFAULT FALSE,
    initiated_by INT NOT NULL,
    action_id INT NULL COMMENT 'FK to CHALLENGE if invitation_type is Challenge',
    FOREIGN KEY (initiated_by) REFERENCES USER(user_id) ON DELETE CASCADE,
    FOREIGN KEY (action_id) REFERENCES CHALLENGE(action_id) ON DELETE SET NULL,
    INDEX idx_initiated_by (initiated_by),
    INDEX idx_action_id (action_id),
    INDEX idx_status (status),
    INDEX idx_expires_at (expires_at),
    CONSTRAINT chk_invitation_xor CHECK (
        (target_email IS NOT NULL AND target_phone IS NULL) OR 
        (target_email IS NULL AND target_phone IS NOT NULL)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. FAMILY_GROUP Table
CREATE TABLE FAMILY_GROUP (
    group_id INT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES USER(user_id) ON DELETE CASCADE,
    INDEX idx_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. GROUP_MEMBER Table (M:N relationship)
CREATE TABLE GROUP_MEMBER (
    group_member_id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT NOT NULL,
    user_id INT NOT NULL,
    role ENUM('Admin', 'Caregiver', 'Member') DEFAULT 'Member',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    left_at TIMESTAMP NULL,
    FOREIGN KEY (group_id) REFERENCES FAMILY_GROUP(group_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USER(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_group_user (group_id, user_id),
    INDEX idx_group_id (group_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. MONTHLY_SUMMARY Table
CREATE TABLE MONTHLY_SUMMARY (
    monthly_summary_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    year INT NOT NULL,
    month INT NOT NULL,
    total_steps INT DEFAULT 0,
    total_appointments INT DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_finalized BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES USER(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_year_month (user_id, year, month),
    INDEX idx_user_id (user_id),
    INDEX idx_year_month (year, month),
    CONSTRAINT chk_month_range CHECK (month >= 1 AND month <= 12),
    CONSTRAINT chk_year_range CHECK (year >= 2000 AND year <= 2100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. CAREGIVER_PATIENT Table (Caregivers can monitor patients)
CREATE TABLE CAREGIVER_PATIENT (
    caregiver_patient_id INT AUTO_INCREMENT PRIMARY KEY,
    caregiver_id INT NOT NULL COMMENT 'User with Caregiver role',
    patient_id INT NOT NULL COMMENT 'User with Patient role',
    relationship VARCHAR(50) COMMENT 'e.g., Parent, Spouse, Child, Other',
    status ENUM('Active', 'Inactive', 'Pending') DEFAULT 'Pending',
    linked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP NULL,
    notes TEXT,
    FOREIGN KEY (caregiver_id) REFERENCES USER(user_id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES USER(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_caregiver_patient (caregiver_id, patient_id),
    INDEX idx_caregiver_id (caregiver_id),
    INDEX idx_patient_id (patient_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

