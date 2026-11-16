package task05;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Task 05 - Benchmark & Alternative Implementations:
 * Benchmarks 10,000 concurrent booking attempts under high contention across 4 strategies:
 * 1. Synchronized Method
 * 2. Synchronized Block
 * 3. ReentrantLock
 * 4. Lock-free AtomicInteger CAS (compareAndSet)
 *
 * Saves raw nanoTime measurements in booking_benchmark.csv.
 */
public class BookingBenchmark {

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("==================================================");
        System.out.println("   TASK 05: BENCHMARKING 10,000 BOOKING ATTEMPTS  ");
        System.out.println("   Student: K VENKATA VAMSI GOUD (192472021)      ");
        System.out.println("==================================================");

        int totalAttempts = 10000;
        int seats = 500;

        String csvPath = "CSA09-192472021-Assignment3/Task05_Race_Condition/results/booking_benchmark.csv";
        PrintWriter csv = new PrintWriter(new FileWriter(csvPath));
        csv.println("Strategy,TotalAttempts,SeatsAvailable,SeatsSold,TotalTimeMs,AvgTimePerAttemptNs");

        // Warmup JVM
        runSyncMethod(1000, 50);

        // 1. Synchronized Method
        BenchmarkResult r1 = runSyncMethod(totalAttempts, seats);
        System.out.printf("1. Synchronized Method : Time=%4d ms | Avg=%7.2f ns/attempt | Sold=%d\n",
                r1.durationMs, r1.avgNsPerAttempt, r1.seatsSold);
        csv.printf("SynchronizedMethod,%d,%d,%d,%d,%.2f\n",
                totalAttempts, seats, r1.seatsSold, r1.durationMs, r1.avgNsPerAttempt);

        // 2. Synchronized Block
        BenchmarkResult r2 = runSyncBlock(totalAttempts, seats);
        System.out.printf("2. Synchronized Block  : Time=%4d ms | Avg=%7.2f ns/attempt | Sold=%d\n",
                r2.durationMs, r2.avgNsPerAttempt, r2.seatsSold);
        csv.printf("SynchronizedBlock,%d,%d,%d,%d,%.2f\n",
                totalAttempts, seats, r2.seatsSold, r2.durationMs, r2.avgNsPerAttempt);

        // 3. ReentrantLock
        BenchmarkResult r3 = runReentrantLock(totalAttempts, seats);
        System.out.printf("3. ReentrantLock       : Time=%4d ms | Avg=%7.2f ns/attempt | Sold=%d\n",
                r3.durationMs, r3.avgNsPerAttempt, r3.seatsSold);
        csv.printf("ReentrantLock,%d,%d,%d,%d,%.2f\n",
                totalAttempts, seats, r3.seatsSold, r3.durationMs, r3.avgNsPerAttempt);

        // 4. Lock-free Atomic CAS
        BenchmarkResult r4 = runAtomic(totalAttempts, seats);
        System.out.printf("4. AtomicInteger (CAS) : Time=%4d ms | Avg=%7.2f ns/attempt | Sold=%d\n",
                r4.durationMs, r4.avgNsPerAttempt, r4.seatsSold);
        csv.printf("AtomicInteger_CAS,%d,%d,%d,%d,%.2f\n",
                totalAttempts, seats, r4.seatsSold, r4.durationMs, r4.avgNsPerAttempt);

        csv.close();
        System.out.println("\nBenchmark results successfully exported to " + csvPath);
        System.out.println("==================================================");
    }

    static class BenchmarkResult {
        long durationMs;
        double avgNsPerAttempt;
        int seatsSold;
        public BenchmarkResult(long durationMs, double avgNsPerAttempt, int seatsSold) {
            this.durationMs = durationMs;
            this.avgNsPerAttempt = avgNsPerAttempt;
            this.seatsSold = seatsSold;
        }
    }

    private static BenchmarkResult runSyncMethod(int attempts, int initialSeats) throws InterruptedException {
        class BookingSystem {
            int remaining = initialSeats;
            int sold = 0;
            synchronized boolean book() {
                if (remaining > 0) {
                    remaining--;
                    sold++;
                    return true;
                }
                return false;
            }
        }
        BookingSystem sys = new BookingSystem();
        CountDownLatch latch = new CountDownLatch(1);
        Thread[] threads = new Thread[attempts];
        for (int i = 0; i < attempts; i++) {
            threads[i] = new Thread(() -> {
                try { latch.await(); sys.book(); } catch (Exception ignored) {}
            });
            threads[i].start();
        }
        long start = System.nanoTime();
        latch.countDown();
        for (Thread t : threads) t.join();
        long end = System.nanoTime();
        long totalNs = end - start;
        return new BenchmarkResult(totalNs / 1_000_000, (double) totalNs / attempts, sys.sold);
    }

    private static BenchmarkResult runSyncBlock(int attempts, int initialSeats) throws InterruptedException {
        class BookingSystem {
            int remaining = initialSeats;
            int sold = 0;
            final Object lock = new Object();
            boolean book() {
                synchronized (lock) {
                    if (remaining > 0) {
                        remaining--;
                        sold++;
                        return true;
                    }
                    return false;
                }
            }
        }
        BookingSystem sys = new BookingSystem();
        CountDownLatch latch = new CountDownLatch(1);
        Thread[] threads = new Thread[attempts];
        for (int i = 0; i < attempts; i++) {
            threads[i] = new Thread(() -> {
                try { latch.await(); sys.book(); } catch (Exception ignored) {}
            });
            threads[i].start();
        }
        long start = System.nanoTime();
        latch.countDown();
        for (Thread t : threads) t.join();
        long end = System.nanoTime();
        long totalNs = end - start;
        return new BenchmarkResult(totalNs / 1_000_000, (double) totalNs / attempts, sys.sold);
    }

    private static BenchmarkResult runReentrantLock(int attempts, int initialSeats) throws InterruptedException {
        class BookingSystem {
            int remaining = initialSeats;
            int sold = 0;
            final ReentrantLock lock = new ReentrantLock();
            boolean book() {
                lock.lock();
                try {
                    if (remaining > 0) {
                        remaining--;
                        sold++;
                        return true;
                    }
                    return false;
                } finally {
                    lock.unlock();
                }
            }
        }
        BookingSystem sys = new BookingSystem();
        CountDownLatch latch = new CountDownLatch(1);
        Thread[] threads = new Thread[attempts];
        for (int i = 0; i < attempts; i++) {
            threads[i] = new Thread(() -> {
                try { latch.await(); sys.book(); } catch (Exception ignored) {}
            });
            threads[i].start();
        }
        long start = System.nanoTime();
        latch.countDown();
        for (Thread t : threads) t.join();
        long end = System.nanoTime();
        long totalNs = end - start;
        return new BenchmarkResult(totalNs / 1_000_000, (double) totalNs / attempts, sys.sold);
    }

    private static BenchmarkResult runAtomic(int attempts, int initialSeats) throws InterruptedException {
        class BookingSystem {
            final AtomicInteger remaining = new AtomicInteger(initialSeats);
            final AtomicInteger sold = new AtomicInteger(0);
            boolean book() {
                while (true) {
                    int curr = remaining.get();
                    if (curr <= 0) return false;
                    if (remaining.compareAndSet(curr, curr - 1)) {
                        sold.incrementAndGet();
                        return true;
                    }
                }
            }
        }
        BookingSystem sys = new BookingSystem();
        CountDownLatch latch = new CountDownLatch(1);
        Thread[] threads = new Thread[attempts];
        for (int i = 0; i < attempts; i++) {
            threads[i] = new Thread(() -> {
                try { latch.await(); sys.book(); } catch (Exception ignored) {}
            });
            threads[i].start();
        }
        long start = System.nanoTime();
        latch.countDown();
        for (Thread t : threads) t.join();
        long end = System.nanoTime();
        long totalNs = end - start;
        return new BenchmarkResult(totalNs / 1_000_000, (double) totalNs / attempts, sys.sold.get());
    }
}
