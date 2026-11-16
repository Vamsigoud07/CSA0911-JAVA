package task02;

import java.io.IOException;

/**
 * Task 02 - Student B Evidence:
 * Demonstrating the difference between Chained Exceptions vs Unchained Exceptions.
 *
 * Chained Exception preserves the original root cause (e.g., IOException: Disk full)
 * within the high-level domain exception (SubmissionStorageException).
 * Unchained Exception discards the root cause, making it impossible in production logs
 * to distinguish between disk full, permission denied, or bad sector hardware faults.
 */
public class ChainedVsUnchainedDemo {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   STUDENT B: CHAINED VS UNCHAINED EXCEPTION DEMO ");
        System.out.println("==================================================");

        System.out.println("\n--- 1. UNCHAINED EXCEPTION (Poor Practice: Root Cause Lost) ---");
        try {
            simulateUnchainedFailure();
        } catch (SubmissionStorageException e) {
            System.out.println("Caught Exception: " + e.getMessage());
            System.out.println("getCause() returns: " + e.getCause());
            System.out.println("Notice: Root cause information is completely LOST to operations teams!");
        }

        System.out.println("\n--- 2. CHAINED EXCEPTION (Best Practice: Full Cause Intact) ---");
        try {
            simulateChainedFailure();
        } catch (SubmissionStorageException e) {
            System.out.println("Caught Exception: " + e.getMessage());
            System.out.println("getCause() returns: " + e.getCause());
            System.out.println("Printing Full Chained Stack Trace (Root Cause Preserved):");
            e.printStackTrace(System.out);
        }
    }

    private static void simulateUnchainedFailure() throws SubmissionStorageException {
        try {
            throw new IOException("Kernel error: NFS mount point timed out [EIO 5]");
        } catch (IOException io) {
            // Anti-pattern: Cause is NOT passed to constructor
            throw new SubmissionStorageException("Could not store answer script");
        }
    }

    private static void simulateChainedFailure() throws SubmissionStorageException {
        try {
            throw new IOException("Kernel error: NFS mount point timed out [EIO 5]");
        } catch (IOException io) {
            // Best pattern: Cause IS chained via constructor
            throw new SubmissionStorageException("Could not store answer script", io);
        }
    }
}
