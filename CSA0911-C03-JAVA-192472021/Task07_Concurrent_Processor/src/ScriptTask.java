package task07;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Callable task processing a single answer-script file.
 * Returns StudentResult on success, or throws ResultFileException on failure.
 */
public class ScriptTask implements Callable<StudentResult> {
    private final String filePath;

    public ScriptTask(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public StudentResult call() throws Exception {
        File file = new File(filePath);
        String fileName = file.getName();

        // 1. Check file existence (Handles missing files)
        if (!file.exists()) {
            throw new ResultFileException(fileName, "ERR_FILE_MISSING", "File not found on filesystem.");
        }

        // 2. Read and parse content
        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {

            String header = br.readLine(); // Header line: StudentID,StudentName,TheoryMarks,LabMarks,AssignmentMarks
            if (header == null) {
                throw new ResultFileException(fileName, "ERR_EMPTY_FILE", "File is empty.");
            }

            String dataLine = br.readLine();
            if (dataLine == null || dataLine.trim().isEmpty()) {
                throw new ResultFileException(fileName, "ERR_NO_DATA", "Missing data row in script file.");
            }

            String[] tokens = dataLine.split(",");
            if (tokens.length < 5) {
                throw new ResultFileException(fileName, "ERR_MALFORMED_CSV",
                        "Expected 5 columns, found " + tokens.length + ": " + dataLine);
            }

            String studentId = tokens[0].trim();
            String studentName = tokens[1].trim();

            int theory;
            int lab;
            int assignment;

            try {
                theory = Integer.parseInt(tokens[2].trim());
                lab = Integer.parseInt(tokens[3].trim());
                assignment = Integer.parseInt(tokens[4].trim());
            } catch (NumberFormatException nfe) {
                throw new ResultFileException(fileName, "ERR_NON_NUMERIC_MARKS",
                        "Non-numeric marks encountered: " + nfe.getMessage(), nfe);
            }

            // 3. Business rule range validation
            if (theory < 0 || theory > 50) {
                throw new ResultFileException(fileName, "ERR_OUT_OF_RANGE",
                        "Theory marks out of range [0-50]: " + theory);
            }
            if (lab < 0 || lab > 30) {
                throw new ResultFileException(fileName, "ERR_OUT_OF_RANGE",
                        "Lab marks out of range [0-30]: " + lab);
            }
            if (assignment < 0 || assignment > 20) {
                throw new ResultFileException(fileName, "ERR_OUT_OF_RANGE",
                        "Assignment marks out of range [0-20]: " + assignment);
            }

            return new StudentResult(studentId, studentName, theory, lab, assignment);

        } catch (IOException io) {
            throw new ResultFileException(fileName, "ERR_IO_FAILURE", "I/O error reading file: " + io.getMessage(), io);
        }
    }
}
