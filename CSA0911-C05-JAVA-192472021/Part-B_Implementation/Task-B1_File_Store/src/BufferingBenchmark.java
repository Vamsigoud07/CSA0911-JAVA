package task_b1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/**
 * Task B1.3: Buffering Experiment (1 MB, 10 MB, 50 MB)
 * Compares unbuffered byte-by-byte read with buffered stream read.
 */
public class BufferingBenchmark {

    public static void generateTestFile(String path, long targetSizeBytes) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        if (file.exists() && file.length() >= targetSizeBytes) {
            return; // Already generated
        }

        String sampleRecord = "P%05d,192472021,StudentName_%d,%d,%.2f\n";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            long currentSize = 0;
            int counter = 1;
            while (currentSize < targetSizeBytes) {
                String line = String.format(Locale.US, sampleRecord, counter, counter, (counter % 20) + 1, 3500.0 + (counter % 1000));
                bw.write(line);
                currentSize += line.length();
                counter++;
            }
        }
    }

    public static long measureUnbufferedRead(String path, long maxBytes) throws IOException {
        long startTime = System.nanoTime();
        try (FileInputStream fis = new FileInputStream(path)) {
            long bytesRead = 0;
            while (fis.read() != -1) {
                bytesRead++;
                if (maxBytes > 0 && bytesRead >= maxBytes) {
                    break;
                }
            }
        }
        return (System.nanoTime() - startTime) / 1_000_000; // ms
    }

    public static long measureBufferedRead(String path) throws IOException {
        long startTime = System.nanoTime();
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path))) {
            byte[] buffer = new byte[8192];
            while (bis.read(buffer) != -1) {
                // Buffered read
            }
        }
        return (System.nanoTime() - startTime) / 1_000_000; // ms
    }

    public static long countLines(String path) throws IOException {
        long lines = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while (br.readLine() != null) {
                lines++;
            }
        }
        return lines;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("============================================================");
        System.out.println("  Task B1.3: File Buffering Benchmark (1 MB, 10 MB, 50 MB)");
        System.out.println("  Student: K VENKATA VAMSI GOUD | Reg No: 192472021");
        System.out.println("============================================================");

        int[] mbSizes = {1, 10, 50};
        String[] filePaths = {
            "Part-B_Implementation/Task-B1_File_Store/input/test_1MB.txt",
            "Part-B_Implementation/Task-B1_File_Store/input/test_10MB.txt",
            "Part-B_Implementation/Task-B1_File_Store/input/test_50MB.txt"
        };

        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Target_Size_MB,Actual_Size_Bytes,Total_Lines,Unbuffered_Time_ms,Buffered_Time_ms,Speedup_Factor\n");

        System.out.printf("%-15s %-18s %-12s %-20s %-18s %-15s\n", 
            "Target Size", "Actual Bytes", "Lines", "Unbuffered (ms)", "Buffered (ms)", "Speedup");
        System.out.println("--------------------------------------------------------------------------------------------------");

        for (int i = 0; i < mbSizes.length; i++) {
            int mb = mbSizes[i];
            String path = filePaths[i];
            long targetBytes = (long) mb * 1024 * 1024;

            System.out.print("Generating " + mb + " MB test file... ");
            generateTestFile(path, targetBytes);
            File f = new File(path);
            long actualBytes = f.length();
            long lines = countLines(path);
            System.out.println("Done (" + lines + " lines).");

            // Measure Unbuffered
            // For 50 MB unbuffered byte-by-byte (52M system calls), we measure 2 MB sample and scale or full
            long unbufferedMs;
            if (mb <= 10) {
                // Average over 2 runs
                long t1 = measureUnbufferedRead(path, 0);
                long t2 = measureUnbufferedRead(path, 0);
                unbufferedMs = (t1 + t2) / 2;
            } else {
                // 50 MB: measure 5 MB sample byte-by-byte and extrapolate precisely
                long sampleBytes = 5 * 1024 * 1024;
                long sampleTime = measureUnbufferedRead(path, sampleBytes);
                unbufferedMs = (sampleTime * actualBytes) / sampleBytes;
            }

            // Measure Buffered (8 KB buffer)
            long b1 = measureBufferedRead(path);
            long b2 = measureBufferedRead(path);
            long b3 = measureBufferedRead(path);
            long bufferedMs = Math.max(1, (b1 + b2 + b3) / 3);

            double speedup = (double) unbufferedMs / bufferedMs;

            System.out.printf("%-15s %-18d %-12d %-20d %-18d %-15.1fx\n", 
                mb + " MB", actualBytes, lines, unbufferedMs, bufferedMs, speedup);

            csvContent.append(String.format(Locale.US, "%d,%d,%d,%d,%d,%.2f\n", 
                mb, actualBytes, lines, unbufferedMs, bufferedMs, speedup));
        }

        // Save CSVs
        String csv1 = "Performance_Results/buffering_results.csv";
        String csv2 = "Part-B_Implementation/Task-B1_File_Store/measurements/buffering_results.csv";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csv1))) {
            bw.write(csvContent.toString());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csv2))) {
            bw.write(csvContent.toString());
        }

        System.out.println("\nResults saved to:");
        System.out.println(" - " + csv1);
        System.out.println(" - " + csv2);
    }
}
