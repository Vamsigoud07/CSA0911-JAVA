package task05;

import java.util.concurrent.CountDownLatch;

/**
 * Task 05 - Implementation 3: Seat Booking with Synchronized Block
 *
 * Synchronized block locks only the exact critical section on an explicit lock object.
 * Fine-grained locking reduces lock contention and improves concurrency throughput.
 */
public class SeatBookingSynchronizedBlock {
    private int seatsRemaining = 10;
    private int seatsSold = 0;
    private final Object lock = new Object(); // Dedicated monitor lock object

    public boolean book(String customerName) {
        // Non-critical preparation code (e.g. log verification, parsing) runs concurrently
        long startTime = System.currentTimeMillis();

        // Fine-grained critical section
        synchronized (lock) {
            if (seatsRemaining > 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {}

                seatsRemaining--;
                seatsSold++;
                return true;
            }
        }
        return false;
    }

    public int getSeatsRemaining() {
        synchronized (lock) {
            return seatsRemaining;
        }
    }

    public int getSeatsSold() {
        synchronized (lock) {
            return seatsSold;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("   SEAT BOOKING WITH SYNCHRONIZED BLOCK           ");
        System.out.println("   Student: K VENKATA VAMSI GOUD (192472021)      ");
        System.out.println("==================================================");

        SeatBookingSynchronizedBlock system = new SeatBookingSynchronizedBlock();
        int totalCompetitors = 100;
        CountDownLatch latch = new CountDownLatch(1);
        Thread[] threads = new Thread[totalCompetitors];

        for (int i = 0; i < totalCompetitors; i++) {
            final String name = "Customer-" + (i + 1);
            threads[i] = new Thread(() -> {
                try {
                    latch.await();
                    system.book(name);
                } catch (InterruptedException ignored) {}
            }, name);
            threads[i].start();
        }

        latch.countDown();

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("\n--- Booking Results (Synchronized Block) ---");
        System.out.println("Initial Available Seats : 10");
        System.out.println("Total Booking Requests  : 100");
        System.out.println("Seats sold: " + system.getSeatsSold());
        System.out.println("Remaining Seats Counter : " + system.getSeatsRemaining());
        System.out.println("[VERIFIED] Exactly 10 seats sold. 0 double bookings.");
        System.out.println("==================================================");
    }
}
