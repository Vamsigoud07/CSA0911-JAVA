package task02;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Three-Layer Online Examination Portal Architecture:
 * Layer 1: ExamController (Entry point, parameter validation, HTTP/CLI simulation)
 * Layer 2: ExamService (Business logic, registration & exam window verification)
 * Layer 3: ExamStorage (Disk I/O, file persistence, wraps low-level IOExceptions)
 */
class ExamStorage {
    public void persistSubmission(String regNo, String examId, String fileName, byte[] data)
            throws SubmissionStorageException {
        try {
            // Simulate storage failure for specific scenario
            if (fileName.contains("corrupt_disk") || fileName.contains("disk_full")) {
                throw new IOException("POSIX write failure: Disk quota exceeded on /var/exam_storage");
            }
            System.out.println("[STORAGE LAYER] Successfully wrote " + data.length + " bytes to file: " + fileName);
        } catch (IOException ioEx) {
            // Exception Chaining: Wrap checked low-level IOException inside checked SubmissionStorageException
            throw new SubmissionStorageException(
                    "Storage Layer Failure: Unable to persist answer-script '" + fileName + "' for student " + regNo,
                    ioEx);
        }
    }
}

class ExamService {
    private final ExamStorage storage = new ExamStorage();
    private static final Set<String> REGISTERED_STUDENTS = new HashSet<>();
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB limit
    private static final boolean EXAM_WINDOW_ACTIVE = true;

    static {
        // Enrolled students for CSA09
        REGISTERED_STUDENTS.add("192472021"); // Student A
        REGISTERED_STUDENTS.add("192472022"); // Student B
        REGISTERED_STUDENTS.add("192472023");
    }

    public void processSubmission(String regNo, String examId, String fileName, long fileSizeBytes,
                                  boolean simulateWindowClosed, boolean simulateDiskFailure)
            throws ExamException, SubmissionStorageException {

        System.out.println("[SERVICE LAYER] Validating submission parameters for Student: " + regNo);

        // Precondition 1: File size validation (Unchecked Exception)
        if (fileSizeBytes > MAX_FILE_SIZE_BYTES) {
            throw new FileSizeExceededException(
                    "File size " + (fileSizeBytes / (1024 * 1024)) + " MB exceeds the strict limit of 5 MB.",
                    fileSizeBytes, MAX_FILE_SIZE_BYTES);
        }

        // Precondition 2: Registration validation (Checked Exception)
        if (!REGISTERED_STUDENTS.contains(regNo)) {
            throw new StudentNotRegisteredException(
                    "Access Denied: Student with Register Number '" + regNo + "' is not enrolled in exam " + examId,
                    regNo);
        }

        // Precondition 3: Exam window validation (Checked Exception)
        if (simulateWindowClosed) {
            long currentEpoch = System.currentTimeMillis();
            throw new ExamWindowClosedException(
                    "Submission Window Expired: Exam portal closed at deadline for Exam ID: " + examId,
                    examId, currentEpoch);
        }

        // Call Storage Layer
        String targetFile = simulateDiskFailure ? "corrupt_disk_" + fileName : fileName;
        byte[] dummyData = new byte[(int) Math.min(fileSizeBytes, 1024)];
        storage.persistSubmission(regNo, examId, targetFile, dummyData);

        System.out.println("[SERVICE LAYER] Answer script verified and forwarded to archival store.");
    }
}

public class ExamPortalApp {
    private final ExamService service = new ExamService();

    public void submitExam(String regNo, String examId, String fileName, long fileSizeBytes,
                           boolean simulateClosed, boolean simulateDiskError) {
        System.out.println("\n=======================================================");
        System.out.println("[CONTROLLER LAYER] Received Submission Request");
        System.out.println("  Student Reg No : " + regNo);
        System.out.println("  Exam Code      : " + examId);
        System.out.println("  File Name      : " + fileName);
        System.out.println("  File Size      : " + (fileSizeBytes / 1024) + " KB");
        System.out.println("=======================================================");

        try {
            // Propagating down to Service and Storage
            service.processSubmission(regNo, examId, fileName, fileSizeBytes, simulateClosed, simulateDiskError);
            System.out.println("[CONTROLLER LAYER] Result: SUBMISSION ACCEPTED (Status Code: 200 OK)");

        } catch (FileSizeExceededException e) {
            // Handled Unchecked Exception
            System.err.println("[CONTROLLER CATCH - FileSizeExceededException] (HTTP 413 Payload Too Large)");
            System.err.println("  User Message   : " + e.getMessage());
            System.err.println("  Actual Size    : " + e.getActualSizeBytes() + " bytes (Max: " + e.getMaxAllowedBytes() + ")");

        } catch (StudentNotRegisteredException e) {
            // Handled Checked Exception
            System.err.println("[CONTROLLER CATCH - StudentNotRegisteredException] (HTTP 403 Forbidden)");
            System.err.println("  User Message   : " + e.getMessage());
            System.err.println("  Failed Reg No  : " + e.getRegisterNumber());

        } catch (ExamWindowClosedException e) {
            // Handled Checked Exception
            System.err.println("[CONTROLLER CATCH - ExamWindowClosedException] (HTTP 410 Gone / Window Closed)");
            System.err.println("  User Message   : " + e.getMessage());
            System.err.println("  Closed at Time : " + e.getClosedTimestamp());

        } catch (SubmissionStorageException e) {
            // Handled Chained Checked Exception
            System.err.println("[CONTROLLER CATCH - SubmissionStorageException] (HTTP 500 Internal Storage Error)");
            System.err.println("  High-level Msg : " + e.getMessage());
            Throwable cause = e.getCause();
            if (cause != null) {
                System.err.println("  Root Cause Type: " + cause.getClass().getName());
                System.err.println("  Root Cause Msg : " + cause.getMessage());
            }

        } catch (ExamException e) {
            // General Domain Exception Fallback
            System.err.println("[CONTROLLER CATCH - ExamException] General Exam Portal Error: " + e.getMessage());

        } finally {
            System.out.println("[CONTROLLER FINALLY] Audit session closed. Response dispatched to client.");
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   ONLINE EXAMINATION PORTAL - 3 LAYER SYSTEM     ");
        System.out.println("   Student: K VENKATA VAMSI GOUD (192472021)      ");
        System.out.println("==================================================");

        ExamPortalApp controller = new ExamPortalApp();

        // Scenario 1: Normal Valid Submission
        System.out.println("\n>>> SCENARIO 1: Valid Student Submission <<<");
        controller.submitExam("192472021", "CSA09-CO3", "vamsi_answersheet.pdf", 2 * 1024 * 1024, false, false);

        // Scenario 2: Unregistered Student
        System.out.println("\n>>> SCENARIO 2: Unregistered Student Failure <<<");
        controller.submitExam("192479999", "CSA09-CO3", "guest_answersheet.pdf", 1 * 1024 * 1024, false, false);

        // Scenario 3: Exam Window Closed
        System.out.println("\n>>> SCENARIO 3: Late Submission (Window Closed) <<<");
        controller.submitExam("192472021", "CSA09-CO3", "vamsi_late_submission.pdf", 1 * 1024 * 1024, true, false);

        // Scenario 4: File Size Exceeded (Unchecked)
        System.out.println("\n>>> SCENARIO 4: Oversized File Upload <<<");
        controller.submitExam("192472021", "CSA09-CO3", "huge_media_recording.pdf", 12 * 1024 * 1024, false, false);

        // Scenario 5: Chained Storage Failure (Root cause: IOException)
        System.out.println("\n>>> SCENARIO 5: Storage Layer Failure with Exception Chaining <<<");
        controller.submitExam("192472021", "CSA09-CO3", "disk_full_answersheet.pdf", 1 * 1024 * 1024, false, true);
    }
}
