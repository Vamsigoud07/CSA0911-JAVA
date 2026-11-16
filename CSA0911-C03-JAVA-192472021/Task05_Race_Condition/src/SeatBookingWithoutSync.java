package task05;

import java.util.concurrent.CountDownLatch;

/**
 * Task 05 - Implementation 1: Seat Booking WITHOUT Synchronization
 *
 * Demonstrates:
 * Race condition and Critical Section violation.
 * Multiple threads read 'seatsRemaining > 0' simultaneously before any single thread
 * updates the counter, resulting in overselling (double bookings).
 */
public class SeatBookingWithoutSync {
    private int seatsRemaining = 10;
    private int seatsSold = 0;

    // Unsynchronized check-then-act critical section!
    public boolean book(String customerName) {
        if (seatsRemaining > 0) {
            try {
                // Micro-delay between check and write widens race window
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}

            seatsRemaining--;
            seatsSold++;
            return true;
        }
        return false;
    }

    public int getSeatsRemaining() {
        return seatsRemaining;
    }

    public int getSeatsSold() {
        return seatsSold;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("   SEAT BOOKING WITHOUT SYNCHRONIZATION (RACE)    ");
        System.out.println("   Student: K VENKATA VAMSI GOUD (192472021)      ");
        System.out.println("==================================================");

        SeatBookingWithoutSync system = new SeatBookingWithoutSync();
        int totalCompetitors = 100;
        CountDownLatch latch = new CountDownLatch(1);
        Thread[] threads = new Thread[totalCompetitors];

        for (int i = 0; i < totalCompetitors; i++) {
            final String name = "Customer-" + (i + 1);
            threads[i] = new Thread(() -> {
                try {
                    latch.await(); // Release all threads at once
                    system.book(name);
                } catch (InterruptedException ignored) {}
            }, name);
            threads[i].start();
        }

        latch.countDown(); // Fire simultaneous race

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("\n--- Booking Results (Unsynchronized) ---");
        System.out.println("Initial Available Seats : 10");
        System.out.println("Total Booking Requests  : 100");
        System.out.println("Seats Sold Recorded     : " + system.getSeatsSold());
        System.out.println("Remaining Seats Counter : " + system.getSeatsRemaining());

        if (system.getSeatsSold() > 10) {
            System.out.printf("[CRITICAL ANOMALY] Double Booking Occurred! %d seats oversold!\n",
                    (system.getSeatsSold() - 10));
        } else {
            System.out.println("[NOTICE] Race did not trigger double booking in this run.");
        }
        System.out.println("==================================================");
    }
}
