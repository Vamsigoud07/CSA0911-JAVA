package com.campus.dao;

import com.campus.exception.ValidationException;
import com.campus.model.Event;
import com.campus.model.Registration;
import com.campus.model.Student;
import com.campus.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Data Access Object for Campus Event Registrations.
 * Provides:
 * - Transactional registration with capacity checking and atomic seat decrement
 * - Parameterized PreparedStatement queries protecting against SQL Injection
 * - Resource deallocation via try-with-resources
 * - Dual execution mode: Real MySQL JDBC when online, resilient synchronized fallback when offline
 */
public class RegistrationDAO {

    // Fallback store simulating relational database state
    private final Map<String, Student> fallbackStudents = new ConcurrentHashMap<>();
    private final Map<String, Event> fallbackEvents = new ConcurrentHashMap<>();
    private final List<Registration> fallbackRegistrations = new CopyOnWriteArrayList<>();

    public RegistrationDAO() {
        initFallbackData();
    }

    private void initFallbackData() {
        fallbackEvents.put("EVT-101", new Event("EVT-101", "National Hackathon 2026", LocalDate.of(2026, 12, 10), "Main Auditorium", 119));
        fallbackEvents.put("EVT-102", new Event("EVT-102", "AI & Robotics Symposium", LocalDate.of(2026, 12, 15), "Tech Park Seminar Hall", 79));
        fallbackEvents.put("EVT-103", new Event("EVT-103", "Java Cloud Microservices Workshop", LocalDate.of(2026, 12, 20), "Computing Lab 4", 39));
        fallbackEvents.put("EVT-104", new Event("EVT-104", "Cyber Security CTF Challenge", LocalDate.of(2027, 1, 8), "Systems Lab 1", 30));
        fallbackEvents.put("EVT-105", new Event("EVT-105", "Annual Cultural Carnival - Milan", LocalDate.of(2027, 1, 25), "Open Air Amphitheatre", 2));

        Student s1 = new Student("192472002", "Aditi Sharma", "aditi.sharma@simats.edu", "Information Technology");
        Student s2 = new Student("192472015", "Rahul Nair", "rahul.nair@simats.edu", "Electronics & Communication");
        Student s3 = new Student("192472044", "Sneha Patel", "sneha.patel@simats.edu", "Computer Science & Engineering");

        fallbackStudents.put(s1.getStudentId(), s1);
        fallbackStudents.put(s2.getStudentId(), s2);
        fallbackStudents.put(s3.getStudentId(), s3);

        fallbackRegistrations.add(new Registration("REG-A10101", s1.getStudentId(), s1.getStudentName(), s1.getEmail(), s1.getDepartment(), "EVT-101", "National Hackathon 2026", LocalDateTime.now().minusDays(2)));
        fallbackRegistrations.add(new Registration("REG-A10102", s2.getStudentId(), s2.getStudentName(), s2.getEmail(), s2.getDepartment(), "EVT-102", "AI & Robotics Symposium", LocalDateTime.now().minusDays(1)));
        fallbackRegistrations.add(new Registration("REG-A10103", s3.getStudentId(), s3.getStudentName(), s3.getEmail(), s3.getDepartment(), "EVT-103", "Java Cloud Microservices Workshop", LocalDateTime.now().minusHours(12)));
    }

    public List<Event> getAllEvents() throws SQLException {
        if (!DatabaseConnection.isMySqlAvailable()) {
            return new ArrayList<>(fallbackEvents.values());
        }

        List<Event> list = new ArrayList<>();
        String sql = "SELECT event_id, event_name, event_date, venue, available_seats FROM events ORDER BY event_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Event(
                        rs.getString("event_id"),
                        rs.getString("event_name"),
                        rs.getDate("event_date").toLocalDate(),
                        rs.getString("venue"),
                        rs.getInt("available_seats")
                ));
            }
        }
        return list;
    }

    public List<Registration> getAllRegistrations() throws SQLException {
        if (!DatabaseConnection.isMySqlAvailable()) {
            return new ArrayList<>(fallbackRegistrations);
        }

        List<Registration> list = new ArrayList<>();
        String sql = "SELECT r.registration_id, r.student_id, s.student_name, s.email, s.department, "
                   + "r.event_id, e.event_name, r.registration_date "
                   + "FROM registrations r "
                   + "JOIN students s ON r.student_id = s.student_id "
                   + "JOIN events e ON r.event_id = e.event_id "
                   + "ORDER BY r.registration_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Registration(
                        rs.getString("registration_id"),
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getString("event_id"),
                        rs.getString("event_name"),
                        rs.getTimestamp("registration_date").toLocalDateTime()
                ));
            }
        }
        return list;
    }

    public List<Registration> getRegistrationsByEvent(String eventId) throws SQLException {
        if (!DatabaseConnection.isMySqlAvailable()) {
            if (eventId == null || eventId.isEmpty() || "ALL".equalsIgnoreCase(eventId)) {
                return new ArrayList<>(fallbackRegistrations);
            }
            List<Registration> res = new ArrayList<>();
            for (Registration r : fallbackRegistrations) {
                if (r.getEventId().equalsIgnoreCase(eventId)) {
                    res.add(r);
                }
            }
            return res;
        }

        List<Registration> list = new ArrayList<>();
        String sql = "SELECT r.registration_id, r.student_id, s.student_name, s.email, s.department, "
                   + "r.event_id, e.event_name, r.registration_date "
                   + "FROM registrations r "
                   + "JOIN students s ON r.student_id = s.student_id "
                   + "JOIN events e ON r.event_id = e.event_id "
                   + "WHERE r.event_id = ? "
                   + "ORDER BY r.registration_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Registration(
                            rs.getString("registration_id"),
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getString("email"),
                            rs.getString("department"),
                            rs.getString("event_id"),
                            rs.getString("event_name"),
                            rs.getTimestamp("registration_date").toLocalDateTime()
                    ));
                }
            }
        }
        return list;
    }

    public boolean isDuplicateRegistration(String studentId, String eventId) throws SQLException {
        if (!DatabaseConnection.isMySqlAvailable()) {
            for (Registration r : fallbackRegistrations) {
                if (r.getStudentId().equalsIgnoreCase(studentId) && r.getEventId().equalsIgnoreCase(eventId)) {
                    return true;
                }
            }
            return false;
        }

        String sql = "SELECT COUNT(*) FROM registrations WHERE student_id = ? AND event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Executes atomic, transactional registration:
     * 1. Checks available capacity
     * 2. Checks duplicate constraint
     * 3. Inserts/updates student record
     * 4. Inserts registration record
     * 5. Decrements available seats
     * 6. Commits transaction
     */
    public synchronized Registration registerStudentForEvent(Student student, String eventId)
            throws SQLException, ValidationException {

        // Check duplicates first
        if (isDuplicateRegistration(student.getStudentId(), eventId)) {
            throw new ValidationException("Duplicate Registration: Student (" + student.getStudentId() +
                    ") is already registered for Event (" + eventId + ").");
        }

        if (!DatabaseConnection.isMySqlAvailable()) {
            // Relational transactional emulation in fallback store
            Event event = fallbackEvents.get(eventId);
            if (event == null) {
                throw new ValidationException("Event Not Found: " + eventId);
            }
            if (event.getAvailableSeats() <= 0) {
                throw new ValidationException("Capacity Exhausted: No available seats remaining for " + event.getEventName());
            }

            // Decrement capacity
            event.setAvailableSeats(event.getAvailableSeats() - 1);
            fallbackStudents.put(student.getStudentId(), student);

            String regId = "REG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Registration reg = new Registration(regId, student.getStudentId(), student.getStudentName(),
                    student.getEmail(), student.getDepartment(), eventId, event.getEventName(), LocalDateTime.now());
            fallbackRegistrations.add(0, reg);
            return reg;
        }

        // REAL MYSQL TRANSACTIONAL EXECUTION
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            // 1. Lock and check event capacity
            String capSql = "SELECT event_name, available_seats FROM events WHERE event_id = ? FOR UPDATE";
            String eventName;
            int seats;
            try (PreparedStatement ps = conn.prepareStatement(capSql)) {
                ps.setString(1, eventId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new ValidationException("Event Not Found: " + eventId);
                    }
                    eventName = rs.getString("event_name");
                    seats = rs.getInt("available_seats");
                }
            }

            if (seats <= 0) {
                conn.rollback();
                throw new ValidationException("Capacity Exhausted: No available seats remaining for " + eventName);
            }

            // 2. Upsert Student
            String studentSql = "INSERT INTO students (student_id, student_name, email, department) "
                              + "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE student_name = VALUES(student_name), "
                              + "email = VALUES(email), department = VALUES(department)";
            try (PreparedStatement ps = conn.prepareStatement(studentSql)) {
                ps.setString(1, student.getStudentId());
                ps.setString(2, student.getStudentName());
                ps.setString(3, student.getEmail());
                ps.setString(4, student.getDepartment());
                ps.executeUpdate();
            }

            // 3. Insert Registration Record
            String regId = "REG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String regSql = "INSERT INTO registrations (registration_id, student_id, event_id, registration_date) "
                          + "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
            try (PreparedStatement ps = conn.prepareStatement(regSql)) {
                ps.setString(1, regId);
                ps.setString(2, student.getStudentId());
                ps.setString(3, eventId);
                ps.executeUpdate();
            }

            // 4. Decrement available seats
            String updateSeatSql = "UPDATE events SET available_seats = available_seats - 1 WHERE event_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSeatSql)) {
                ps.setString(1, eventId);
                ps.executeUpdate();
            }

            conn.commit(); // Commit Transaction

            return new Registration(regId, student.getStudentId(), student.getStudentName(),
                    student.getEmail(), student.getDepartment(), eventId, eventName, LocalDateTime.now());

        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            if (ex.getErrorCode() == 1062) { // MySQL ER_DUP_ENTRY
                throw new ValidationException("Duplicate Registration: Database constraint uk_student_event rejected duplicate booking.");
            }
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }
}
