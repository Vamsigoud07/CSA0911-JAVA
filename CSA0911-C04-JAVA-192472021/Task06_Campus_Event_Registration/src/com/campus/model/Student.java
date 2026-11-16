package com.campus.model;

import java.io.Serializable;

/**
 * Entity representing a Student enrolled in the university.
 */
public class Student implements Serializable {
    private final String studentId;
    private final String studentName;
    private final String email;
    private final String department;

    public Student(String studentId, String studentName, String email, String department) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.department = department;
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }

    @Override
    public String toString() {
        return studentName + " (" + studentId + ")";
    }
}
