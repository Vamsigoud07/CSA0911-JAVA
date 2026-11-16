package task05;

/**
 * Task 05 - Mandatory "Complete the Code" Program: SeatBooking.java
 *
 * Demonstrates:
 * 1. Race condition during check-then-act on shared remaining seats counter.
 * 2. Atomic check-and-decrement using synchronization.
 *
 * Expected Synchronized Output:
 * Seats sold: 10
 */
public class SeatBooking {
    private int seatsRemaining = 10;
    private int seatsSold = 0;

    // TODO 1: Implement book() method to handle concurrent booking requests safely
    public synchronized boolean book(String customerName) {
        if (seatsRemaining > 0) {
            try {
                // Micro-pause representing seat reservation validation
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
        SeatBooking bookingSystem = new SeatBooking();
        int totalThreads = 100;
        Thread[] threads = new Thread[totalThreads];

        for (int i = 0; i < totalThreads; i++) {
            final String customer = "User-" + (i + 1);
            threads[i] = new Thread(() -> bookingSystem.book(customer));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("Seats sold: " + bookingSystem.getSeatsSold());
    }
}
