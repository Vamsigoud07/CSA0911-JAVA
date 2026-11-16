package task07;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task 07 - Alternative Concept 2: One Thread Per File (Anti-pattern Demonstration)
 *
 * Demonstrates the severe hazards of spawning a distinct native thread per file.
 * While tested here on a safe subset (1,000 files), attempting this for 10,000 files
 * demands 10,000 * 1 MB = 10 GB of virtual stack memory, precipitating:
 * "java.lang.OutOfMemoryError: unable to create new native thread"
 * and massive kernel context-switching thrashing.
 */
public class ThreadPerFileResultProcessor {

    public static void main(String[] args) throws Exception {
        System.out.println("==================================================");
        System.out.println("   ALTERNATIVE 2: ONE THREAD PER FILE HAZARD DEMO ");
        System.out.println("==================================================");

        String inputDir = "CSA09-192472021-Assignment3/Task07_Concurrent_Processor/input";
        List<String> files = GenerateScoreDataset.generateFiles(inputDir);

        int sampleSize = 1000; // Demonstrating on a bounded subset of 1,000 files
        List<String> subset = files.subList(0, sampleSize);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        System.out.printf("Spawning %d individual native OS threads simultaneously...\n", sampleSize);
        List<Thread> threads = new ArrayList<>(sampleSize);

        long start = System.currentTimeMillis();

        for (String path : subset) {
            Thread t = new Thread(() -> {
                try {
                    ScriptTask task = new ScriptTask(path);
                    StudentResult res = task.call();
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        long duration = System.currentTimeMillis() - start;
        System.out.printf("Thread-per-file finished %d files in %d ms\n", sampleSize, duration);
        System.out.printf("  Success: %d | Failed: %d\n", successCount.get(), failCount.get());
        System.out.println("Warning: Scaling to 10,000 threads risks kernel exhaustion!");
        System.out.println("==================================================");
    }
}
