package task03;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Task 03 - Implementation 1: Manual Resource Closing with try-catch-finally
 *
 * Demonstrates:
 * 1. Explicit resource closing in finally blocks.
 * 2. Nested try-catch in finally required to handle IOException from close().
 * 3. Resource leak vulnerability: If the first close() throws an exception,
 *    subsequent close() calls are skipped unless cumbersome individual try-catch blocks are written.
 */
public class ManualCloseReport {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   DAILY SALES REPORT - MANUAL CLOSE (TRY-FINALLY)");
        System.out.println("   Student: K VENKATA VAMSI GOUD (192472021)      ");
        System.out.println("==================================================");

        List<String> storeFiles = new ArrayList<>();
        storeFiles.add("CSA09-192472021-Assignment3/Task03_Resource_Safety/input/store1_chennai.csv");
        storeFiles.add("CSA09-192472021-Assignment3/Task03_Resource_Safety/input/store2_bangalore.csv");
        storeFiles.add("CSA09-192472021-Assignment3/Task03_Resource_Safety/input/store3_hyderabad_corrupt.csv");
        storeFiles.add("CSA09-192472021-Assignment3/Task03_Resource_Safety/input/missing_store.csv");
        storeFiles.add("CSA09-192472021-Assignment3/Task03_Resource_Safety/input/store4_mumbai.csv");

        String reportOutput = "CSA09-192472021-Assignment3/Task03_Resource_Safety/results/manual_sales_report.txt";

        processAllStores(storeFiles, reportOutput);
    }

    public static void processAllStores(List<String> files, String summaryPath) {
        PrintWriter writer = null;
        FileWriter fw = null;
        double grandTotal = 0.0;
        int successfulFiles = 0;
        int failedFiles = 0;

        try {
            fw = new FileWriter(summaryPath);
            writer = new PrintWriter(fw);
            writer.println("==================================================");
            writer.println("   DAILY REGIONAL SALES SUMMARY (MANUAL CLOSE)   ");
            writer.println("==================================================");

            for (String filePath : files) {
                File file = new File(filePath);
                System.out.println("\n[MANUAL CLOSE] Opening file: " + file.getName());

                BufferedReader reader = null;
                FileReader fr = null;
                boolean fileSuccess = false;
                double storeTotal = 0.0;
                int validTxns = 0;

                try {
                    if (!file.exists()) {
                        throw new IOException("File does not exist: " + file.getPath());
                    }

                    fr = new FileReader(file);
                    reader = new BufferedReader(fr);

                    String line;
                    int lineNum = 0;
                    while ((line = reader.readLine()) != null) {
                        lineNum++;
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue; // Skip comments and empty lines
                        }
                        String[] parts = line.split(",");
                        if (parts.length < 2) {
                            throw new IllegalArgumentException("Malformed CSV line " + lineNum + ": " + line);
                        }
                        double amount = Double.parseDouble(parts[1].trim());
                        storeTotal += amount;
                        validTxns++;
                    }

                    grandTotal += storeTotal;
                    successfulFiles++;
                    fileSuccess = true;
                    System.out.printf("[SUCCESS] %s: Processed %d transactions. Store Total: INR %.2f\n",
                            file.getName(), validTxns, storeTotal);
                    writer.printf("%-30s | Valid Txns: %3d | Total: INR %10.2f | Status: OK\n",
                            file.getName(), validTxns, storeTotal);

                } catch (Exception ex) {
                    failedFiles++;
                    System.err.printf("[FAILED] Error processing file '%s': %s\n", file.getName(), ex.getMessage());
                    writer.printf("%-30s | Status: FAILED (%s)\n", file.getName(), ex.getMessage());
                } finally {
                    // Manual resource cleanup for reader
                    System.out.println("[FINALLY] Manually closing resources for " + file.getName());
                    if (reader != null) {
                        try {
                            reader.close();
                            System.out.println("  -> BufferedReader closed successfully.");
                        } catch (IOException e) {
                            System.err.println("  -> WARNING: Failed to close BufferedReader: " + e.getMessage());
                        }
                    }
                    if (fr != null) {
                        try {
                            fr.close();
                            System.out.println("  -> FileReader closed successfully.");
                        } catch (IOException e) {
                            System.err.println("  -> WARNING: Failed to close FileReader: " + e.getMessage());
                        }
                    }
                }
            }

            writer.println("--------------------------------------------------");
            writer.printf("Total Successful Stores: %d\n", successfulFiles);
            writer.printf("Total Failed Stores    : %d\n", failedFiles);
            writer.printf("GRAND TOTAL SALES      : INR %.2f\n", grandTotal);
            writer.println("==================================================");

        } catch (IOException e) {
            System.err.println("[CRITICAL] Could not write summary file: " + e.getMessage());
        } finally {
            // Manual resource cleanup for writer
            System.out.println("\n[FINALLY] Closing summary report writer resources...");
            if (writer != null) {
                writer.close();
                System.out.println("  -> PrintWriter closed.");
            }
            if (fw != null) {
                try {
                    fw.close();
                    System.out.println("  -> FileWriter closed.");
                } catch (IOException e) {
                    System.err.println("  -> Failed to close FileWriter: " + e.getMessage());
                }
            }
        }
    }
}
