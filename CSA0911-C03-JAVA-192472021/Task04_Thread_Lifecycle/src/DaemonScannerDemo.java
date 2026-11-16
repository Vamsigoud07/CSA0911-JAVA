package task04;

/**
 * Task 04 - Student B Evidence: Daemon Thread Demonstration
 *
 * A daemon thread is a low-priority background thread (such as JVM Garbage Collector)
 * whose sole purpose is to provide services to user threads.
 * When all user threads terminate, the JVM shuts down immediately,
 * killing any active daemon threads even if their loops have not completed.
 */
public class DaemonScannerDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("   STUDENT B: DAEMON THREAD DEMONSTRATION         ");
        System.out.println("==================================================");

        // Background hardware temperature & optical diagnostic monitor (Daemon)
        Thread diagnosticDaemon = new Thread(() -> {
            int tick = 0;
            while (true) {
                tick++;
                System.out.println("  [DAEMON MONITOR] Hardware diagnostics check #" + tick + " (OK)");
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "HardwareDiagnosticsDaemon");

        // Set as daemon BEFORE calling start()
        diagnosticDaemon.setDaemon(true);
        System.out.println("Is diagnosticDaemon a daemon? " + diagnosticDaemon.isDaemon());
        diagnosticDaemon.start();

        // Main user task (Worker thread)
        Thread mainScanner = new Thread(() -> {
            System.out.println("[USER SCANNER] Processing high-priority answer script batch...");
            for (int i = 1; i <= 3; i++) {
                System.out.println("[USER SCANNER] Scanned batch " + i + "/3");
                try {
                    Thread.sleep(60);
                } catch (InterruptedException ignored) {}
            }
            System.out.println("[USER SCANNER] User thread finished. Exiting scanner worker.");
        }, "UserScannerThread");

        mainScanner.start();
        mainScanner.join(); // Wait for user thread to finish

        System.out.println("\n[MAIN EXIT] Main method reached the end.");
        System.out.println("Since no user threads remain, the JVM exits immediately.");
        System.out.println("The daemon thread is automatically aborted without completing its infinite loop.");
        System.out.println("==================================================");
    }
}
