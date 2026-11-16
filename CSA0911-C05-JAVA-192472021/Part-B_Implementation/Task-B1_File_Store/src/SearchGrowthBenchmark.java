package task_b1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/**
 * Task B1.4: File Search Growth Experiment
 * Evaluates O(N) sequential scanning across 1,000, 10,000, 50,000, and 100,000 records.
 */
public class SearchGrowthBenchmark {

    public static void generateDataset(String path, int numRecords) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        if (file.exists()) {
            // Check line count
            int lines = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                while (br.readLine() != null) lines++;
            }
            if (lines >= numRecords) return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (int i = 1; i <= numRecords; i++) {
                // PassId, RegNo, Name, Route, Amount
                bw.write(String.format(Locale.US, "P%06d,19247%04d,Student_%d,%d,%.2f\n",
                    i, (2000 + (i % 8000)), i, (i % 25) + 1, 3000.0 + (i % 2000)));
            }
        }
    }

    public static long measureSearchNano(String path, String targetId) throws IOException {
        long start = System.nanoTime();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].trim().equalsIgnoreCase(targetId)) {
                    break; // Found
                }
            }
        }
        return System.nanoTime() - start;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("============================================================");
        System.out.println("  Task B1.4: Search Growth Experiment (1K to 100K Records)");
        System.out.println("  Student: K VENKATA VAMSI GOUD | Reg No: 192472021");
        System.out.println("============================================================");

        int[] recordCounts = {1_000, 10_000, 50_000, 100_000};
        String[] filePaths = {
            "Part-B_Implementation/Task-B1_File_Store/input/passes_1k.txt",
            "Part-B_Implementation/Task-B1_File_Store/input/passes_10k.txt",
            "Part-B_Implementation/Task-B1_File_Store/input/passes_50k.txt",
            "Part-B_Implementation/Task-B1_File_Store/input/passes_100k.txt"
        };

        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Record_Count,File_Size_Bytes,Early_Search_ms,Middle_Search_ms,Late_Search_ms,Missing_Search_ms\n");

        System.out.printf("%-12s %-15s %-18s %-18s %-18s %-18s\n",
            "Records", "File Size", "Early Find (ms)", "Mid Find (ms)", "Late Find (ms)", "Missing (ms)");
        System.out.println("------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < recordCounts.length; i++) {
            int count = recordCounts[i];
            String path = filePaths[i];

            System.out.print("Generating " + count + " records... ");
            generateDataset(path, count);
            long fileBytes = new File(path).length();
            System.out.println("Done (" + fileBytes + " bytes).");

            String earlyId = "P000005";
            String midId = String.format(Locale.US, "P%06d", count / 2);
            String lateId = String.format(Locale.US, "P%06d", count - 2);
            String missingId = "P9999999";

            // Run 5 warmups & repetitions
            long earlyNanos = 0, midNanos = 0, lateNanos = 0, missingNanos = 0;
            int trials = 3;
            for (int t = 0; t < trials; t++) {
                earlyNanos += measureSearchNano(path, earlyId);
                midNanos += measureSearchNano(path, midId);
                lateNanos += measureSearchNano(path, lateId);
                missingNanos += measureSearchNano(path, missingId);
            }

            double earlyMs = (earlyNanos / trials) / 1_000_000.0;
            double midMs = (midNanos / trials) / 1_000_000.0;
            double lateMs = (lateNanos / trials) / 1_000_000.0;
            double missingMs = (missingNanos / trials) / 1_000_000.0;

            System.out.printf("%-12d %-15d %-18.3f %-18.3f %-18.3f %-18.3f\n",
                count, fileBytes, earlyMs, midMs, lateMs, missingMs);

            csvContent.append(String.format(Locale.US, "%d,%d,%.3f,%.3f,%.3f,%.3f\n",
                count, fileBytes, earlyMs, midMs, lateMs, missingMs));
        }

        String csv1 = "Performance_Results/file_search_results.csv";
        String csv2 = "Part-B_Implementation/Task-B1_File_Store/measurements/file_search_results.csv";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csv1))) {
            bw.write(csvContent.toString());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csv2))) {
            bw.write(csvContent.toString());
        }

        System.out.println("\nSearch results saved to:");
        System.out.println(" - " + csv1);
        System.out.println(" - " + csv2);
    }
}
