package task07;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Deterministic dataset generator for Task 07:
 * Generates 10,000 student answer-script records with controlled, injected anomalies:
 * - 9,700 Valid records
 * - 200 Injected corrupt records (malformed columns, non-numeric values, marks out of range)
 * - 100 Missing files (referenced in processing batch but intentionally omitted from disk)
 *
 * Random Seed: 42 (Guarantees 100% deterministic reproducibility).
 */
public class GenerateScoreDataset {
    public static final long SEED = 42L;
    public static final int TOTAL_RECORDS = 10000;
    public static final int VALID_COUNT = 9700;
    public static final int CORRUPT_COUNT = 200;
    public static final int MISSING_COUNT = 100;

    public static List<String> generateFiles(String inputDir) throws IOException {
        File dir = new File(inputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Random rand = new Random(SEED);
        List<String> filePaths = new ArrayList<>(TOTAL_RECORDS);

        System.out.printf("Generating deterministic dataset of %d score files (Seed: %d)...\n", TOTAL_RECORDS, SEED);

        // 1. Generate 9,700 Valid Files
        for (int i = 1; i <= VALID_COUNT; i++) {
            String fileName = String.format("script_valid_%05d.csv", i);
            File file = new File(dir, fileName);
            filePaths.add(file.getPath());

            int theory = 20 + rand.nextInt(31);     // 20-50
            int lab = 15 + rand.nextInt(16);        // 15-30
            int assign = 10 + rand.nextInt(11);     // 10-20

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file), 512)) {
                bw.write("StudentID,StudentName,TheoryMarks,LabMarks,AssignmentMarks\n");
                bw.write(String.format("19247%04d,Student_%05d,%d,%d,%d\n", i, i, theory, lab, assign));
            }
        }

        // 2. Generate 200 Injected Corrupt Files
        for (int i = 1; i <= CORRUPT_COUNT; i++) {
            String fileName = String.format("script_corrupt_%04d.csv", i);
            File file = new File(dir, fileName);
            filePaths.add(file.getPath());

            int corruptionType = i % 4;
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file), 512)) {
                bw.write("StudentID,StudentName,TheoryMarks,LabMarks,AssignmentMarks\n");
                if (corruptionType == 0) {
                    // Non-numeric score
                    bw.write(String.format("19247%04d,Student_Corrupt_%d,FORTY_TWO,25,18\n", (9700 + i), i));
                } else if (corruptionType == 1) {
                    // Missing columns
                    bw.write(String.format("19247%04d,Student_Corrupt_%d,45\n", (9700 + i), i));
                } else if (corruptionType == 2) {
                    // Out-of-range marks (Theory > 50)
                    bw.write(String.format("19247%04d,Student_Corrupt_%d,99,25,18\n", (9700 + i), i));
                } else {
                    // Empty body
                    // (Header only)
                }
            }
        }

        // 3. Add 100 Missing File References (Intentionally NOT created on disk)
        for (int i = 1; i <= MISSING_COUNT; i++) {
            String fileName = String.format("script_missing_%04d.csv", i);
            File file = new File(dir, fileName);
            // DO NOT WRITE FILE TO DISK
            filePaths.add(file.getPath());
        }

        System.out.printf("Dataset ready: %d Valid files on disk, %d Corrupt files on disk, %d Missing file references.\n",
                VALID_COUNT, CORRUPT_COUNT, MISSING_COUNT);

        return filePaths;
    }

    public static void main(String[] args) throws IOException {
        String inputDir = "CSA09-192472021-Assignment3/Task07_Concurrent_Processor/input";
        generateFiles(inputDir);
    }
}
