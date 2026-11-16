package task02;

/**
 * Task 02 - Alternative Concept:
 * Converting Custom Checked Exceptions to Unchecked Exceptions (Extending RuntimeException).
 *
 * Demonstrates:
 * 1. Clean method signatures: methods no longer require 'throws' clauses.
 * 2. Calling code is not forced by the compiler to use try-catch blocks.
 * 3. Trade-off: Compiler no longer verifies that exceptional conditions are handled.
 */
class UncheckedExamException extends RuntimeException {
    public UncheckedExamException(String message) {
        super(message);
    }
    public UncheckedExamException(String message, Throwable cause) {
        super(message, cause);
    }
}

class UncheckedStudentNotRegisteredException extends UncheckedExamException {
    public UncheckedStudentNotRegisteredException(String message) {
        super(message);
    }
}

public class UncheckedExamDemo {

    // Method signature has NO 'throws' clause!
    public static void processUnchecked(String regNo) {
        if (!"192472021".equals(regNo)) {
            throw new UncheckedStudentNotRegisteredException("Unchecked: Student " + regNo + " not registered.");
        }
        System.out.println("Student " + regNo + " successfully verified without checked throws clauses.");
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   ALTERNATIVE: UNCHECKED RUNTIME EXCEPTION DEMO  ");
        System.out.println("==================================================");

        // Valid execution
        processUnchecked("192472021");

        // Exceptional execution handled optionally
        try {
            processUnchecked("192479999");
        } catch (UncheckedExamException e) {
            System.out.println("Caught Unchecked Exception at controller level: " + e.getMessage());
        }

        System.out.println("\nSignatures are cleaner, but developers must exercise discipline");
        System.out.println("to ensure critical business failures are not left unhandled.");
    }
}
