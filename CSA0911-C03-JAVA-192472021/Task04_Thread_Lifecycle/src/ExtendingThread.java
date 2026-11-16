package task04;

/**
 * Task 04 - Implementation 1: Creating Threads by Extending java.lang.Thread
 *
 * Simulates four answer-script scanner stations processing batches in parallel.
 * Demonstrates: start(), run(), sleep(), join(), isAlive(), getState(), and priorities.
 */
class ScannerStationThread extends Thread {
    private final String stationName;
    private final int totalScripts;
    private int scannedCount = 0;

    public ScannerStationThread(String stationName, int totalScripts, int priority) {
        super(stationName);
        this.stationName = stationName;
        this.totalScripts = totalScripts;
        setPriority(priority);
    }

    public int getScannedCount() {
        return scannedCount;
    }

    @Override
    public void run() {
        System.out.printf("[%s] Started scanning %d scripts on Thread [%s, Priority: %d, State: %s]\n",
                stationName, totalScripts, getName(), getPriority(), getState());

        for (int i = 1; i <= totalScripts; i++) {
            scannedCount++;
            try {
                // Simulate physical optical scanner feed latency
                Thread.sleep(15);
            } catch (InterruptedException e) {
                System.err.printf("[%s] Scanner interrupted during operation!\n", stationName);
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.printf("[%s] Finished batch! Total scanned: %d scripts.\n", stationName, scannedCount);
    }
}

public class ExtendingThread {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   THREAD LIFECYCLE: EXTENDING java.lang.Thread   ");
        System.out.println("   Student: K VENKATA VAMSI GOUD (192472021)      ");
        System.out.println("==================================================");

        // Instantiate 4 scanner station threads with varying priorities
        ScannerStationThread s1 = new ScannerStationThread("Station-1", 50, Thread.MIN_PRIORITY);  // Priority 1
        ScannerStationThread s2 = new ScannerStationThread("Station-2", 50, Thread.NORM_PRIORITY); // Priority 5
        ScannerStationThread s3 = new ScannerStationThread("Station-3", 50, Thread.NORM_PRIORITY); // Priority 5
        ScannerStationThread s4 = new ScannerStationThread("Station-4", 50, Thread.MAX_PRIORITY);  // Priority 10

        // Demonstrate NEW state and isAlive()
        System.out.println("\n--- State Before start() ---");
        System.out.printf("Station-1: State = %s | isAlive() = %b\n", s1.getState(), s1.isAlive());
        System.out.printf("Station-4: State = %s | isAlive() = %b\n", s4.getState(), s4.isAlive());

        // Start all threads
        System.out.println("\n--- Starting All 4 Scanner Threads ---");
        s1.start();
        s2.start();
        s3.start();
        s4.start();

        // Sample state while running
        try {
            Thread.sleep(5);
            System.out.println("\n--- Sample State While Running ---");
            System.out.printf("Station-1: State = %s | isAlive() = %b\n", s1.getState(), s1.isAlive());
            System.out.printf("Station-4: State = %s | isAlive() = %b\n", s4.getState(), s4.isAlive());
        } catch (InterruptedException ignored) {}

        // Wait for all scanner threads to finish using join()
        System.out.println("\n--- Main Thread Waiting via join() ---");
        try {
            s1.join();
            s2.join();
            s3.join();
            s4.join();
        } catch (InterruptedException e) {
            System.err.println("Main thread interrupted while waiting.");
        }

        // Verify completion and TERMINATED state
        System.out.println("\n--- State After join() ---");
        System.out.printf("Station-1: State = %s | isAlive() = %b | Scanned: %d\n", s1.getState(), s1.isAlive(), s1.getScannedCount());
        System.out.printf("Station-2: State = %s | isAlive() = %b | Scanned: %d\n", s2.getState(), s2.isAlive(), s2.getScannedCount());
        System.out.printf("Station-3: State = %s | isAlive() = %b | Scanned: %d\n", s3.getState(), s3.isAlive(), s3.getScannedCount());
        System.out.printf("Station-4: State = %s | isAlive() = %b | Scanned: %d\n", s4.getState(), s4.isAlive(), s4.getScannedCount());

        int grandTotal = s1.getScannedCount() + s2.getScannedCount() + s3.getScannedCount() + s4.getScannedCount();
        System.out.printf("\n[FINAL SCANNER AUDIT] Total answer scripts successfully digitized: %d / 200\n", grandTotal);
        System.out.println("==================================================");
    }
}
