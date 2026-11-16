package com.campus.service;

import com.campus.dao.RegistrationDAO;
import com.campus.exception.ValidationException;
import com.campus.model.Event;
import com.campus.model.Registration;
import com.campus.model.Student;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service Layer for Campus Event Registration.
 * Handles validation rules, business logic, CSV exporting, and DAO delegation.
 */
public class RegistrationService {
    private final RegistrationDAO dao;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{5,20}$");

    public RegistrationService(RegistrationDAO dao) {
        this.dao = dao;
    }

    public RegistrationService() {
        this(new RegistrationDAO());
    }

    public List<Event> getEvents() throws SQLException {
        return dao.getAllEvents();
    }

    public List<Registration> getAllRegistrations() throws SQLException {
        return dao.getAllRegistrations();
    }

    public List<Registration> filterRegistrationsByEvent(String eventId) throws SQLException {
        return dao.getRegistrationsByEvent(eventId);
    }

    public Registration registerStudent(String studentId, String name, String email,
                                        String department, String eventId)
            throws ValidationException, SQLException {

        // 1. Validation: Student ID
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ValidationException("Validation Error: Student ID cannot be empty.");
        }
        studentId = studentId.trim().toUpperCase();
        if (!STUDENT_ID_PATTERN.matcher(studentId).matches()) {
            throw new ValidationException("Validation Error: Student ID must be alphanumeric (5-20 characters).");
        }

        // 2. Validation: Student Name
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Validation Error: Student Name cannot be empty.");
        }
        name = name.trim();
        if (name.length() < 2 || name.length() > 80) {
            throw new ValidationException("Validation Error: Student Name must be between 2 and 80 characters.");
        }

        // 3. Validation: Email
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Validation Error: Email address cannot be empty.");
        }
        email = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Validation Error: Invalid email format (e.g. name@university.edu).");
        }

        // 4. Validation: Department
        if (department == null || department.trim().isEmpty()) {
            throw new ValidationException("Validation Error: Department must be selected.");
        }

        // 5. Validation: Event ID
        if (eventId == null || eventId.trim().isEmpty() || "SELECT".equalsIgnoreCase(eventId)) {
            throw new ValidationException("Validation Error: An event must be selected.");
        }

        Student student = new Student(studentId, name, email, department.trim());
        return dao.registerStudentForEvent(student, eventId);
    }

    /**
     * Exports registrations to standard RFC-4180 compliant CSV.
     * Guarantees proper quotation and escaping of fields containing commas, quotes, or newlines.
     */
    public int exportRegistrationsToCSV(List<Registration> list, File targetFile) throws IOException {
        if (list == null) {
            throw new IllegalArgumentException("Registration list cannot be null.");
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(targetFile))) {
            // CSV Header
            writer.println("Registration ID,Student ID,Student Name,Email,Department,Event ID,Event Name,Registration Date");

            for (Registration r : list) {
                writer.println(String.join(",",
                        escapeCsv(r.getRegistrationId()),
                        escapeCsv(r.getStudentId()),
                        escapeCsv(r.getStudentName()),
                        escapeCsv(r.getEmail()),
                        escapeCsv(r.getDepartment()),
                        escapeCsv(r.getEventId()),
                        escapeCsv(r.getEventName()),
                        escapeCsv(r.getFormattedDate())
                ));
            }
        }
        return list.size();
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public RegistrationDAO getDao() {
        return dao;
    }
}
