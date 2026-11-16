package task07;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task 07 - Alternative Concept 1: Parallel Streams Result Processor
 *
 * Demonstrates:
 * 1. Java 8 Parallel Streams using ForkJoinPool.commonPool().
 * 2. Declarative pipeline processing.
 * 3. Limitations: Uses common shared pool; hard to configure thread limits; exceptions in stream terminate or require awkward wrapping.
 */
public class ParallelStreamResultProcessor {

    public static void main(String[] args) throws Exception {
        System.out.println("==================================================");
        System.out.println("   ALTERNATIVE 1: PARALLEL STREAMS PROCESSOR      ");
        System.out.println("==================================================");

        String inputDir = "CSA09-192472021-Assignment3/Task07_Concurrent_Processor/input";
        List<String> files = GenerateScoreDataset.generateFiles(inputDir);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        long start = System.currentTimeMillis();

        files.parallelStream().forEach(path -> {
            try {
                ScriptTask task = new ScriptTask(path);
                StudentResult res = task.call();
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        long duration = System.currentTimeMillis() - start;
        System.out.printf("Parallel Stream completed 10,000 files in %d ms\n", duration);
        System.out.printf("  Success: %d | Failed: %d | Total: %d\n",
                successCount.get(), failCount.get(), (successCount.get() + failCount.get()));
        System.out.println("==================================================");
    }
}
