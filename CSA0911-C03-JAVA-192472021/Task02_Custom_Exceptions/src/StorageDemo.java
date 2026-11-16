package task02;

import java.io.IOException;

/**
 * Task 02 - Mandatory "Complete the Code" Program: StorageDemo.java
 *
 * Requirements:
 * Replace TODO 1 with:
 * throw new SubmissionStorageException("Could not store " + fileName, e);
 *
 * Expected output:
 * Saved answer01.pdf
 * Error: Could not store answer02.pdf
 * Cause: Disk quota exceeded
 */
class SubmissionStorageException extends Exception {
    public SubmissionStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubmissionStorageException(String message) {
        super(message);
    }
}

public class StorageDemo {

    public static void storeFile(String fileName) throws SubmissionStorageException {
        try {
            if ("answer02.pdf".equals(fileName)) {
                throw new IOException("Disk quota exceeded");
            }
            // Successfully stored
            System.out.println("Saved " + fileName);
        } catch (IOException e) {
            // TODO 1: Complete the exception chaining
            throw new SubmissionStorageException("Could not store " + fileName, e);
        }
    }

    public static void main(String[] args) {
        String[] files = {"answer01.pdf", "answer02.pdf"};
        for (String file : files) {
            try {
                storeFile(file);
            } catch (SubmissionStorageException e) {
                System.out.println("Error: " + e.getMessage());
                if (e.getCause() != null) {
                    System.out.println("Cause: " + e.getCause().getMessage());
                }
            }
        }
    }
}
