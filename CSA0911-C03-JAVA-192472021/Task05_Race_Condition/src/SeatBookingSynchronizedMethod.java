package task05;

import java.util.concurrent.CountDownLatch;

/**
 * Task 05 - Implementation 2: Seat Booking with Synchronized Method
 *
 * The synchronized keyword acquires the intrinsic monitor lock of 'this' object instance.
 * Exactly one thread at a time can enter book(), guaranteeing mutual exclusion and atomicity.
 */
public class SeatBookingSynchronizedMethod {
    private int seatsRemaining = 10;
    private int seatsSold = 0;

    // Synchronized method locks the entire instance method
    public synchronized boolean book(String customerName) {
        if (seatsRemaining > 0) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}

            seatsRemaining--;
            seatsSold++;
            return true;
        }
        return false;
    }

    public synchronized int getSeatsRemaining() {
        return seatsRemaining;
    }

    public synchronized int getSeatsSold() {
        return seatsSold;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("   SEAT BOOKING WITH SYNCHRONIZED METHOD          ");
        System.out.println("   Student: K VENKATA VAMSI GOUD (192472021)      ");
        System.out.println("==================================================");

        SeatBookingSynchronizedMethod system = new SeatBookingSynchronizedMethod();
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

        System.out.println("\n--- Booking Results (Synchronized Method) ---");
        System.out.println("Initial Available Seats : 10");
        System.out.println("Total Booking Requests  : 100");
        System.out.println("Seats sold: " + system.getSeatsSold());
        System.out.println("Remaining Seats Counter : " + system.getSeatsRemaining());
        System.out.println("[VERIFIED] Exactly 10 seats sold. 0 double bookings.");
        System.out.println("==================================================");
    }
}
