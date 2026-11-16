package task07;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * High-performance concurrent fault-tolerant engine for processing thousands of script tasks.
 */
public class ResultProcessor {

    public static class ProcessBenchmark {
        public int threadCount;
        public long durationMs;
        public int successful;
        public int failed;
        public int total;
        public double throughput; // files per second
        public boolean invariantPassed;
    }

    public static ProcessBenchmark processBatch(List<String> filePaths, int threadCount) throws InterruptedException {
        ProcessingSummary summary = new ProcessingSummary();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Future<StudentResult>> futures = new ArrayList<>(filePaths.size());

        long startNano = System.nanoTime();

        // Submit one Callable task per file
        for (String path : filePaths) {
            summary.incrementSubmitted();
            futures.add(executor.submit(new ScriptTask(path)));
        }

        // Collect Future results
        for (int i = 0; i < futures.size(); i++) {
            Future<StudentResult> future = futures.get(i);
            String path = filePaths.get(i);
            try {
                StudentResult result = future.get();
                summary.recordSuccess(result);
            } catch (ExecutionException ee) {
                // Fault tolerance: Extract cause without failing batch
                Throwable cause = ee.getCause();
                summary.recordFailure(path, cause != null ? cause.getMessage() : ee.getMessage());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                summary.recordFailure(path, "Interrupted during future.get()");
            }
        }

        // Orderly shutdown
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        long elapsedNano = System.nanoTime() - startNano;
        long durationMs = elapsedNano / 1_000_000;
        if (durationMs == 0) durationMs = 1;

        double throughput = ((double) summary.getTotalSubmitted() / durationMs) * 1000.0;

        ProcessBenchmark bm = new ProcessBenchmark();
        bm.threadCount = threadCount;
        bm.durationMs = durationMs;
        bm.successful = summary.getSuccessCount();
        bm.failed = summary.getFailureCount();
        bm.total = summary.getTotalSubmitted();
        bm.throughput = throughput;
        bm.invariantPassed = summary.verifyInvariant();

        return bm;
    }
}
