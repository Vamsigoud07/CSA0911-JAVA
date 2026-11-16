package task07;

/**
 * Custom checked exception representing any parsing, formatting,
 * or business rule failure during answer-script processing.
 */
public class ResultFileException extends Exception {
    private final String fileName;
    private final String errorCode;

    public ResultFileException(String fileName, String errorCode, String message) {
        super(String.format("[%s on %s] %s", errorCode, fileName, message));
        this.fileName = fileName;
        this.errorCode = errorCode;
    }

    public ResultFileException(String fileName, String errorCode, String message, Throwable cause) {
        super(String.format("[%s on %s] %s", errorCode, fileName, message), cause);
        this.fileName = fileName;
        this.errorCode = errorCode;
    }

    public String getFileName() {
        return fileName;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
