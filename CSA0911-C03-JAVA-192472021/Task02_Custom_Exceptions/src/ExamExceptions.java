package task02;

/**
 * Base checked exception for all domain errors within the Online Examination Portal.
 */
class ExamException extends Exception {
    public ExamException(String message) {
        super(message);
    }

    public ExamException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Checked exception thrown when an answer-script submission is attempted outside the active exam window.
 */
class ExamWindowClosedException extends ExamException {
    private final String examId;
    private final long closedTimestamp;

    public ExamWindowClosedException(String message, String examId, long closedTimestamp) {
        super(message);
        this.examId = examId;
        this.closedTimestamp = closedTimestamp;
    }

    public String getExamId() {
        return examId;
    }

    public long getClosedTimestamp() {
        return closedTimestamp;
    }
}

/**
 * Checked exception thrown when an unregistered or blacklisted student attempts to submit an exam.
 */
class StudentNotRegisteredException extends ExamException {
    private final String registerNumber;

    public StudentNotRegisteredException(String message, String registerNumber) {
        super(message);
        this.registerNumber = registerNumber;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }
}

/**
 * Unchecked exception (extends RuntimeException) thrown when an uploaded answer-script exceeds maximum allowed byte limit.
 * Since this represents a client-side precondition failure (bad request), RuntimeException is appropriate.
 */
class FileSizeExceededException extends RuntimeException {
    private final long actualSizeBytes;
    private final long maxAllowedBytes;

    public FileSizeExceededException(String message, long actualSizeBytes, long maxAllowedBytes) {
        super(message);
        this.actualSizeBytes = actualSizeBytes;
        this.maxAllowedBytes = maxAllowedBytes;
    }

    public long getActualSizeBytes() {
        return actualSizeBytes;
    }

    public long getMaxAllowedBytes() {
        return maxAllowedBytes;
    }
}
