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
 * Custom AutoCloseable resource designed to demonstrate Suppressed Exceptions.
 * If close() fails while a primary exception is active in the try block,
 * the JVM attaches this close exception as a 'suppressed exception'
 * via Throwable.addSuppressed() instead of discarding the primary exception.
 */
class MonitoredResource implements AutoCloseable {
    private final String name;
    private final boolean failOnClose;

    public MonitoredResource(String name, boolean failOnClose) {
        this.name = name;
        this.failOnClose = failOnClose;
        System.out.println("  [ACQUIRED] Resource: " + name);
    }

    public void doWork() throws IOException {
        System.out.println("  [WORKING] Processing stream data in: " + name);
    }

    @Override
    public void close() throws IOException {
        System.out.println("  [CLOSING] AutoCloseable.close() invoked on: " + name);
        if (failOnClose) {
            throw new IOException("Secondary Close Failure on resource: " + name);
        }
        System.out.println("  [CLOSED] Resource " + name + " closed cleanly.");
    }
}

/**
 * Task 03 - Implementation 2: Try-With-Resources and Suppressed Exceptions
 *
 * Demonstrates:
 * 1. Idiomatic try-with-resources for file reading and writing.
 * 2. Guaranteed reverse-order closing of resources.
 * 3. Accurate handling of suppressed exceptions via getSuppressed().
 */
public class TryWithResourcesReport {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   DAILY SALES REPORT - TRY-WITH-RESOURCES        ");
        System.out.println("   Student: K VENKATA VAMSI GOUD (192472021)      ");
        System.out.println("==================================================");

        List<String> storeFiles = new ArrayList<>();
        storeFiles.add("CSA09-192472021-Assignment3/Task03_Resource_Safety/input/store1_chennai.csv");
        storeFiles.add("CSA09-192472021-Assignment3/Task03_Resource_Safety/input/store2_bangalore.csv");
        storeFiles.add("CSA09-192472021-Assignment3/Task03_Resource_Safety/input/store3_hyderabad_corrupt.csv");
        storeFiles.add("CSA09-192472021-Assignment3/Task03_Resource_Safety/input/missing_store.csv");
        storeFiles.add("CSA09-192472021-Assignment3/Task03_Resource_Safety/input/store4_mumbai.csv");

        String reportOutput = "CSA09-192472021-Assignment3/Task03_Resource_Safety/results/try_with_resources_sales_report.txt";

        processAllStores(storeFiles, reportOutput);

        // Separate demonstration of suppressed exceptions
        System.out.println("\n--------------------------------------------------");
        System.out.println("   DEMONSTRATING SUPPRESSED EXCEPTIONS IN JAVA    ");
        System.out.println("--------------------------------------------------");
        demonstrateSuppressedExceptions();
    }

    public static void processAllStores(List<String> files, String summaryPath) {
        double grandTotal = 0.0;
        int successfulFiles = 0;
        int failedFiles = 0;

        // Try-with-resources for the summary output file
        try (FileWriter fw = new FileWriter(summaryPath);
             PrintWriter writer = new PrintWriter(fw)) {

            writer.println("==================================================");
            writer.println("  DAILY REGIONAL SALES SUMMARY (TRY-WITH-RESOURCES)");
            writer.println("==================================================");

            for (String filePath : files) {
                File file = new File(filePath);
                System.out.println("\n[TRY-WITH-RESOURCES] Processing: " + file.getName());

                double storeTotal = 0.0;
                int validTxns = 0;

                // Try-with-resources: Both FileReader and BufferedReader are automatically closed in reverse order
                try (FileReader fr = new FileReader(file);
                     BufferedReader reader = new BufferedReader(fr)) {

                    String line;
                    int lineNum = 0;
                    while ((line = reader.readLine()) != null) {
                        lineNum++;
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }
                        String[] parts = line.split(",");
                        if (parts.length < 2) {
                            throw new IllegalArgumentException("Malformed CSV record at line " + lineNum + ": " + line);
                        }
                        double amount = Double.parseDouble(parts[1].trim());
                        storeTotal += amount;
                        validTxns++;
                    }

                    grandTotal += storeTotal;
                    successfulFiles++;
                    System.out.printf("[SUCCESS] %s: Processed %d transactions. Store Total: INR %.2f\n",
                            file.getName(), validTxns, storeTotal);
                    writer.printf("%-30s | Valid Txns: %3d | Total: INR %10.2f | Status: OK\n",
                            file.getName(), validTxns, storeTotal);

                } catch (Exception ex) {
                    failedFiles++;
                    System.err.printf("[FAILED] Exception caught for file '%s': %s\n", file.getName(), ex.getMessage());
                    writer.printf("%-30s | Status: FAILED (%s)\n", file.getName(), ex.getMessage());
                }
            }

            writer.println("--------------------------------------------------");
            writer.printf("Total Successful Stores: %d\n", successfulFiles);
            writer.printf("Total Failed Stores    : %d\n", failedFiles);
            writer.printf("GRAND TOTAL SALES      : INR %.2f\n", grandTotal);
            writer.println("==================================================");

        } catch (IOException e) {
            System.err.println("[CRITICAL] Could not write summary file: " + e.getMessage());
        }

        System.out.println("\n[SUMMARY] All try-with-resources file handles automatically released.");
    }

    public static void demonstrateSuppressedExceptions() {
        System.out.println("Opening two resources where close() throws an error during an active exception...");

        try (MonitoredResource res1 = new MonitoredResource("DatabaseChannel", true);
             MonitoredResource res2 = new MonitoredResource("AuditLogStream", true)) {

            System.out.println("Executing primary business transaction in try block...");
            throw new RuntimeException("PRIMARY FAILURE: Database transaction timed out!");

        } catch (Exception ex) {
            System.out.println("\n[CATCH] Caught Primary Exception: " + ex.getMessage());
            Throwable[] suppressed = ex.getSuppressed();
            System.out.println("Number of Suppressed Exceptions attached: " + suppressed.length);
            for (int i = 0; i < suppressed.length; i++) {
                System.out.println("  -> Suppressed [" + (i + 1) + "]: " + suppressed[i].getMessage());
            }
        }
    }
}
