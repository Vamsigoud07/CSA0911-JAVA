package task07;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe accumulator for multi-threaded batch processing results.
 * Employs lock-free AtomicInteger counters and ConcurrentHashMap for zero-lock contention.
 */
public class ProcessingSummary {
    private final AtomicInteger totalSubmitted = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicLong cumulativeScore = new AtomicLong(0);

    // Records failure details keyed by filename
    private final ConcurrentHashMap<String, String> failureDetails = new ConcurrentHashMap<>();
    // Grade distribution counters
    private final ConcurrentHashMap<String, AtomicInteger> gradeDistribution = new ConcurrentHashMap<>();

    public ProcessingSummary() {
        gradeDistribution.put("S", new AtomicInteger(0));
        gradeDistribution.put("A", new AtomicInteger(0));
        gradeDistribution.put("B", new AtomicInteger(0));
        gradeDistribution.put("C", new AtomicInteger(0));
        gradeDistribution.put("D", new AtomicInteger(0));
        gradeDistribution.put("F", new AtomicInteger(0));
    }

    public void incrementSubmitted() {
        totalSubmitted.incrementAndGet();
    }

    public void recordSuccess(StudentResult res) {
        successCount.incrementAndGet();
        cumulativeScore.addAndGet(res.getTotalMarks());
        gradeDistribution.computeIfAbsent(res.getGrade(), k -> new AtomicInteger(0)).incrementAndGet();
    }

    public void recordFailure(String fileName, String reason) {
        failureCount.incrementAndGet();
        failureDetails.put(fileName, reason);
    }

    public int getTotalSubmitted() { return totalSubmitted.get(); }
    public int getSuccessCount() { return successCount.get(); }
    public int getFailureCount() { return failureCount.get(); }
    public double getAverageScore() {
        int s = successCount.get();
        return s == 0 ? 0.0 : (double) cumulativeScore.get() / s;
    }

    public ConcurrentHashMap<String, String> getFailureDetails() {
        return failureDetails;
    }

    public ConcurrentHashMap<String, AtomicInteger> getGradeDistribution() {
        return gradeDistribution;
    }

    public boolean verifyInvariant() {
        return (successCount.get() + failureCount.get()) == totalSubmitted.get();
    }
}
