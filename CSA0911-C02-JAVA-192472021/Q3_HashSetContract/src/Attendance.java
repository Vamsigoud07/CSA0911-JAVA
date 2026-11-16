package src;

import java.util.HashSet;
import java.util.Set;

/**
 * Q3: Event Attendance De-duplication System
 * Completed code adhering to hashCode and equals contract.
 * Mandatory Expected Output: Unique students: 2
 *
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 */
public class Attendance {

    public static void main(String[] args) {
        Set<Student> attendanceList = new HashSet<>();

        // Three attendance swipes: two students share the same registration number (192472021)
        Student s1 = new Student("192472021", "Vamsi");
        Student s2 = new Student("192472021", "Vamsi Goud"); // Duplicate registration number
        Student s3 = new Student("192472022", "Rahul");

        attendanceList.add(s1);
        attendanceList.add(s2);
        attendanceList.add(s3);

        // Mandatory expected output: Unique students: 2
        System.out.println("Unique students: " + attendanceList.size());
    }
}
