package task05;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;

/**
 * Task 05 - Student A Evidence:
 * Executes 10 trials of Unsynchronized Booking vs Synchronized Booking.
 * Demonstrates that unsynchronized execution produces double bookings (> 10 sold),
 * whereas synchronized execution consistently sells exactly 10 seats.
 */
public class TenRunSeatRace {

    public static void main(String[] args) throws IOException, InterruptedException {
        String csvPath = "CSA09-192472021-Assignment3/Task05_Race_Condition/results/ten_run_race_comparison.csv";
        PrintWriter csv = new PrintWriter(new FileWriter(csvPath));
        csv.println("Trial,UnsyncSeatsSold,UnsyncAnomaly,SyncSeatsSold,SyncCorrect");

        System.out.println("==================================================");
        System.out.println("   TEN-RUN RACE CONDITION REPRODUCTION EXPERIMENT ");
        System.out.println("==================================================");

        for (int trial = 1; trial <= 10; trial++) {
            int unsyncSold = runUnsynchronizedTrial();
            int syncSold = runSynchronizedTrial();

            boolean unsyncAnomaly = unsyncSold > 10;
            boolean syncCorrect = syncSold == 10;

            System.out.printf("Trial #%02d: Unsynchronized Sold = %2d (Oversold? %5b) | Synchronized Sold = %2d (Exact 10? %5b)\n",
                    trial, unsyncSold, unsyncAnomaly, syncSold, syncCorrect);

            csv.printf("%d,%d,%b,%d,%b\n", trial, unsyncSold, unsyncAnomaly, syncSold, syncCorrect);
        }

        csv.close();
        System.out.println("\nResults saved to " + csvPath);
    }

    private static int runUnsynchronizedTrial() throws InterruptedException {
        class UnsyncSystem {
            int remaining = 10;
            int sold = 0;
            void book() {
                if (remaining > 0) {
                    try { Thread.sleep(1); } catch (InterruptedException ignored) {}
                    remaining--;
                    sold++;
                }
            }
        }
        UnsyncSystem sys = new UnsyncSystem();
        CountDownLatch latch = new CountDownLatch(1);
        Thread[] threads = new Thread[100];
        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(() -> {
                try { latch.await(); sys.book(); } catch (Exception ignored) {}
            });
            threads[i].start();
        }
        latch.countDown();
        for (Thread t : threads) t.join();
        return sys.sold;
    }

    private static int runSynchronizedTrial() throws InterruptedException {
        class SyncSystem {
            int remaining = 10;
            int sold = 0;
            synchronized void book() {
                if (remaining > 0) {
                    try { Thread.sleep(1); } catch (InterruptedException ignored) {}
                    remaining--;
                    sold++;
                }
            }
        }
        SyncSystem sys = new SyncSystem();
        CountDownLatch latch = new CountDownLatch(1);
        Thread[] threads = new Thread[100];
        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(() -> {
                try { latch.await(); sys.book(); } catch (Exception ignored) {}
            });
            threads[i].start();
        }
        latch.countDown();
        for (Thread t : threads) t.join();
        return sys.sold;
    }
}
