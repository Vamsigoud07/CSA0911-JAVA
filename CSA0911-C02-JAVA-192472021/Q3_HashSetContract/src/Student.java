package src;

import java.util.Objects;

/**
 * Q3: Student class for Event Attendance De-duplication.
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 */
public class Student implements Comparable<Student> {
    private final String regNo;
    private String name;

    public Student(String regNo, String name) {
        this.regNo = regNo;
        this.name = name;
    }

    public String getRegNo() {
        return regNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Overriding equals() based on student registration number
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(regNo, student.regNo);
    }

    // Overriding hashCode() consistently with equals()
    @Override
    public int hashCode() {
        return Objects.hash(regNo);
    }

    // Natural ordering for TreeSet based on regNo
    @Override
    public int compareTo(Student o) {
        return this.regNo.compareTo(o.regNo);
    }

    @Override
    public String toString() {
        return "Student{" + "regNo='" + regNo + '\'' + ", name='" + name + '\'' + '}';
    }
}
