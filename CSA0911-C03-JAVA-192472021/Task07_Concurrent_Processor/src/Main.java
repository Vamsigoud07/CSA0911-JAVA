package task07;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Task 07 - Main Execution and Benchmark Suite:
 *
 * 1. Executes small hand-checked dataset of 10 files to verify correctness.
 * 2. Benchmarks 10,000 files across 1, 2, 4, and 8 worker threads.
 * 3. Records execution times, successful files, failed files, and throughput.
 * 4. Exports benchmark measurements to CSV for scientific graphing and academic reporting.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("   SEMESTER RESULT PROCESSING SYSTEM - CONCURRENT FAULT-TOLERANT ENGINE        ");
        System.out.println("   Course: CSA09 | Register No: 192472021 | SIMATS Saveetha School of Engg     ");
        System.out.println("================================================================================");

        String inputDir = "CSA09-192472021-Assignment3/Task07_Concurrent_Processor/input";
        String resultsDir = "CSA09-192472021-Assignment3/Task07_Concurrent_Processor/results";

        // Step 1: Ensure 10,000 Dataset Exists
        List<String> allFilePaths = GenerateScoreDataset.generateFiles(inputDir);

        // Step 2: Small Hand-Checked Dataset (10 Files)
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("   STAGE 1: VERIFYING CORRECTNESS ON SMALL HAND-CHECKED DATASET (10 FILES)      ");
        System.out.println("--------------------------------------------------------------------------------");
        List<String> smallBatch = new ArrayList<>(allFilePaths.subList(0, 7)); // 7 valid
        smallBatch.add(allFilePaths.get(9700)); // 1 corrupt
        smallBatch.add(allFilePaths.get(9701)); // 1 corrupt
        smallBatch.add(allFilePaths.get(9900)); // 1 missing

        ResultProcessor.ProcessBenchmark smallBm = ResultProcessor.processBatch(smallBatch, 2);
        System.out.printf("Hand-checked run complete in %d ms:\n", smallBm.durationMs);
        System.out.printf("  Submitted  : %d files\n", smallBm.total);
        System.out.printf("  Successful : %d files\n", smallBm.successful);
        System.out.printf("  Failed     : %d files\n", smallBm.failed);
        System.out.printf("  Invariant (Success + Failed == Total): %b\n", smallBm.invariantPassed);

        // Step 3: Full 10,000 Dataset Benchmark (1, 2, 4, 8 Threads)
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("   STAGE 2: FULL 10,000 FILES BENCHMARK (1, 2, 4, 8 THREADS)                    ");
        System.out.println("--------------------------------------------------------------------------------");

        int[] threadConfigs = {1, 2, 4, 8};
        String csvPath = resultsDir + "/benchmark_results.csv";
        PrintWriter csv = new PrintWriter(new FileWriter(csvPath));
        csv.println("Threads,TotalFiles,SuccessfulFiles,FailedFiles,DurationMs,ThroughputFilesPerSec,InvariantPassed");

        for (int threads : threadConfigs) {
            System.out.printf("\n>>> Running benchmark with %d Thread(s)... <<<\n", threads);
            ResultProcessor.ProcessBenchmark bm = ResultProcessor.processBatch(allFilePaths, threads);

            System.out.printf("  Thread Count  : %d\n", bm.threadCount);
            System.out.printf("  Execution Time: %d ms\n", bm.durationMs);
            System.out.printf("  Successful    : %d files\n", bm.successful);
            System.out.printf("  Failed        : %d files (Controlled Injected Failures)\n", bm.failed);
            System.out.printf("  Throughput    : %.2f files/sec\n", bm.throughput);
            System.out.printf("  Invariant Check (%d + %d == %d): %s\n",
                    bm.successful, bm.failed, bm.total, (bm.invariantPassed ? "PASSED [OK]" : "FAILED"));

            csv.printf("%d,%d,%d,%d,%d,%.2f,%b\n",
                    bm.threadCount, bm.total, bm.successful, bm.failed, bm.durationMs, bm.throughput, bm.invariantPassed);
            csv.flush();
        }

        csv.close();
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("Raw benchmark measurements saved successfully to: " + csvPath);
        System.out.println("================================================================================");
    }
}
