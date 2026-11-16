package com.campus.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entity representing a confirmed registration linking a Student to an Event.
 */
public class Registration implements Serializable {
    private final String registrationId;
    private final String studentId;
    private final String studentName;
    private final String email;
    private final String department;
    private final String eventId;
    private final String eventName;
    private final LocalDateTime registrationDate;

    public Registration(String registrationId, String studentId, String studentName,
                        String email, String department, String eventId,
                        String eventName, LocalDateTime registrationDate) {
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.department = department;
        this.eventId = eventId;
        this.eventName = eventName;
        this.registrationDate = registrationDate != null ? registrationDate : LocalDateTime.now();
    }

    public String getRegistrationId() { return registrationId; }
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }

    public String getFormattedDate() {
        return registrationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
