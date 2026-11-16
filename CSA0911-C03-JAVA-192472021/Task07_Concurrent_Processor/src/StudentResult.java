package task07;

/**
 * Immutable value object holding parsed student score calculations.
 */
public class StudentResult {
    private final String studentId;
    private final String studentName;
    private final int theoryMarks;     // Out of 50
    private final int labMarks;        // Out of 30
    private final int assignmentMarks; // Out of 20
    private final int totalMarks;      // Out of 100
    private final String grade;

    public StudentResult(String studentId, String studentName, int theoryMarks, int labMarks, int assignmentMarks) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.theoryMarks = theoryMarks;
        this.labMarks = labMarks;
        this.assignmentMarks = assignmentMarks;
        this.totalMarks = theoryMarks + labMarks + assignmentMarks;
        this.grade = calculateGrade(this.totalMarks);
    }

    private String calculateGrade(int total) {
        if (total >= 90) return "S";
        if (total >= 80) return "A";
        if (total >= 70) return "B";
        if (total >= 60) return "C";
        if (total >= 50) return "D";
        return "F";
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public int getTheoryMarks() { return theoryMarks; }
    public int getLabMarks() { return labMarks; }
    public int getAssignmentMarks() { return assignmentMarks; }
    public int getTotalMarks() { return totalMarks; }
    public String getGrade() { return grade; }
}
