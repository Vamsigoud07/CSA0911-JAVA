package task04;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task 04 - Alternative Concept:
 * Fixed Thread Pool via java.util.concurrent.ExecutorService.
 *
 * Demonstrates:
 * 1. Worker thread reuse instead of expensive thread creation/destruction.
 * 2. Predictable memory usage bounded by pool size (p threads * 1 MB stack).
 * 3. Work-stealing/queueing of n tasks across p workers.
 */
public class ThreadPoolScanner {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("   ALTERNATIVE: FIXED THREAD POOL SCANNER        ");
        System.out.println("==================================================");

        int totalBatches = 20;
        int poolSize = 4;
        AtomicInteger completedBatches = new AtomicInteger(0);

        ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        System.out.printf("Initialized FixedThreadPool with %d worker threads.\n", poolSize);
        System.out.printf("Submitting %d answer-script scanning batches...\n\n", totalBatches);

        long startNano = System.nanoTime();

        for (int i = 1; i <= totalBatches; i++) {
            final int batchId = i;
            pool.execute(() -> {
                String worker = Thread.currentThread().getName();
                try {
                    Thread.sleep(20); // Simulate processing batch
                    completedBatches.incrementAndGet();
                    System.out.printf("[POOL WORKER: %s] Finished Batch #%02d\n", worker, batchId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Orderly shutdown
        pool.shutdown();
        boolean finished = pool.awaitTermination(5, TimeUnit.SECONDS);
        long elapsedMs = (System.nanoTime() - startNano) / 1_000_000;

        System.out.println("\nAll pool batches completed? " + finished);
        System.out.printf("Total Batches Processed : %d / %d\n", completedBatches.get(), totalBatches);
        System.out.printf("Total Execution Time    : %d ms\n", elapsedMs);
        System.out.println("==================================================");
    }
}
