package task04;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task 04 - Implementation 2: Creating Threads by Implementing java.lang.Runnable
 *
 * Preferred modern approach that decouples the task (scanning logic)
 * from the execution thread mechanism, allowing classes to extend other base classes.
 */
class ScannerStationRunnable implements Runnable {
    private final String stationName;
    private final int totalScripts;
    private final AtomicInteger scannedCount = new AtomicInteger(0);

    public ScannerStationRunnable(String stationName, int totalScripts) {
        this.stationName = stationName;
        this.totalScripts = totalScripts;
    }

    public int getScannedCount() {
        return scannedCount.get();
    }

    @Override
    public void run() {
        Thread current = Thread.currentThread();
        System.out.printf("[%s] Running task on Thread [%s, ID: %d, Priority: %d]\n",
                stationName, current.getName(), current.getId(), current.getPriority());

        for (int i = 1; i <= totalScripts; i++) {
            scannedCount.incrementAndGet();
            try {
                Thread.sleep(12);
            } catch (InterruptedException e) {
                System.err.printf("[%s] Interrupted!\n", stationName);
                current.interrupt();
                break;
            }
        }

        System.out.printf("[%s] Completed scanning %d scripts.\n", stationName, scannedCount.get());
    }
}

public class ImplementingRunnable {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   THREAD LIFECYCLE: IMPLEMENTING Runnable        ");
        System.out.println("   Student: K VENKATA VAMSI GOUD (192472021)      ");
        System.out.println("==================================================");

        ScannerStationRunnable task1 = new ScannerStationRunnable("Station-1", 40);
        ScannerStationRunnable task2 = new ScannerStationRunnable("Station-2", 40);
        ScannerStationRunnable task3 = new ScannerStationRunnable("Station-3", 40);
        ScannerStationRunnable task4 = new ScannerStationRunnable("Station-4", 40);

        Thread t1 = new Thread(task1, "Worker-Thread-1");
        Thread t2 = new Thread(task2, "Worker-Thread-2");
        Thread t3 = new Thread(task3, "Worker-Thread-3");
        Thread t4 = new Thread(task4, "Worker-Thread-4");

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            System.err.println("Main thread interrupted.");
        }

        int total = task1.getScannedCount() + task2.getScannedCount() + task3.getScannedCount() + task4.getScannedCount();
        System.out.println("\nAll runnable scanner tasks completed successfully.");
        System.out.printf("Total processed scripts: %d / 160\n", total);
        System.out.println("==================================================");
    }
}
