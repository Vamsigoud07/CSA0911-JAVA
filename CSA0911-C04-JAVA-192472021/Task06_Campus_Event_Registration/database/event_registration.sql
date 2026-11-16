-- ==============================================================================
-- SIMATS - SAVEETHA SCHOOL OF ENGINEERING
-- PROGRAMMING IN JAVA (CSA09) - ASSIGNMENT 4 (CO4)
-- TASK 6: CAMPUS EVENT REGISTRATION DATABASE SCHEMA
-- Student Name: K VENKATA VAMSI GOUD | Register Number: 192472021
-- ==============================================================================

DROP DATABASE IF EXISTS campus_events_db;
CREATE DATABASE campus_events_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_events_db;

-- 1. Table: Students
CREATE TABLE students (
    student_id VARCHAR(20) PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Table: Events
CREATE TABLE events (
    event_id VARCHAR(20) PRIMARY KEY,
    event_name VARCHAR(150) NOT NULL,
    event_date DATE NOT NULL,
    venue VARCHAR(100) NOT NULL,
    available_seats INT NOT NULL DEFAULT 50,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_seats_non_negative CHECK (available_seats >= 0)
) ENGINE=InnoDB;

-- 3. Table: Registrations
-- CRITICAL DATABASE-LEVEL ENFORCEMENT:
-- uk_student_event guarantees a student cannot register twice for the same event
CREATE TABLE registrations (
    registration_id VARCHAR(36) PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    event_id VARCHAR(20) NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reg_student FOREIGN KEY (student_id) 
        REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_reg_event FOREIGN KEY (event_id) 
        REFERENCES events(event_id) ON DELETE RESTRICT,
    CONSTRAINT uk_student_event UNIQUE (student_id, event_id)
) ENGINE=InnoDB;

-- Indexing for high-performance filtering and queries
CREATE INDEX idx_reg_event ON registrations(event_id);
CREATE INDEX idx_reg_student ON registrations(student_id);

-- ==============================================================================
-- SAMPLE SEED DATA
-- ==============================================================================

-- Seed Campus Events
INSERT INTO events (event_id, event_name, event_date, venue, available_seats) VALUES
('EVT-101', 'National Hackathon 2026', '2026-12-10', 'Main Auditorium', 120),
('EVT-102', 'AI & Robotics Symposium', '2026-12-15', 'Tech Park Seminar Hall', 80),
('EVT-103', 'Java Cloud Microservices Workshop', '2026-12-20', 'Computing Lab 4', 40),
('EVT-104', 'Cyber Security CTF Challenge', '2027-01-08', 'Systems Lab 1', 30),
('EVT-105', 'Annual Cultural Carnival - Milan', '2027-01-25', 'Open Air Amphitheatre', 2); -- Low seats to test capacity exhaustion!

-- Seed Students
INSERT INTO students (student_id, student_name, email, department) VALUES
('192472021', 'K VENKATA VAMSI GOUD', 'vamsigoud192472021@simats.edu', 'Computer Science & Engineering'),
('192472002', 'Aditi Sharma', 'aditi.sharma@simats.edu', 'Information Technology'),
('192472015', 'Rahul Nair', 'rahul.nair@simats.edu', 'Electronics & Communication'),
('192472044', 'Sneha Patel', 'sneha.patel@simats.edu', 'Computer Science & Engineering');

-- Seed Initial Registrations
INSERT INTO registrations (registration_id, student_id, event_id, registration_date) VALUES
('REG-A10101', '192472002', 'EVT-101', CURRENT_TIMESTAMP),
('REG-A10102', '192472015', 'EVT-102', CURRENT_TIMESTAMP),
('REG-A10103', '192472044', 'EVT-103', CURRENT_TIMESTAMP);

-- Update available seats to match seed registrations
UPDATE events SET available_seats = available_seats - 1 WHERE event_id = 'EVT-101';
UPDATE events SET available_seats = available_seats - 1 WHERE event_id = 'EVT-102';
UPDATE events SET available_seats = available_seats - 1 WHERE event_id = 'EVT-103';
