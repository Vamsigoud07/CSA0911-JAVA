package task01;

/**
 * Task 01 - Student B Evidence:
 * Demonstrating Uncaught Exception and Stack Trace Generation.
 *
 * An unchecked exception that is not caught anywhere along the call stack
 * causes the executing thread to terminate abruptly. The JVM uncaught exception
 * handler then outputs the thread name, exception class name, message,
 * and the complete method execution call stack to stderr.
 */
public class UncaughtExceptionDemo {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   DEMONSTRATING UNCAUGHT EXCEPTION BEHAVIOR      ");
        System.out.println("   Student B Evidence - JVM Abrupt Termination    ");
        System.out.println("==================================================");

        System.out.println("Initiating ATM session...");
        methodA();
        System.out.println("This statement will NEVER execute due to uncaught exception.");
    }

    public static void methodA() {
        System.out.println("Entering methodA()...");
        methodB();
        System.out.println("Exiting methodA()...");
    }

    public static void methodB() {
        System.out.println("Entering methodB() - ATM internal calculation...");
        // Deliberate unchecked exception: ArithmeticException (divide by zero)
        int balance = 5000;
        int zeroFactor = 0;
        int result = balance / zeroFactor; // Throws ArithmeticException: / by zero
        System.out.println("Result: " + result);
    }
}
