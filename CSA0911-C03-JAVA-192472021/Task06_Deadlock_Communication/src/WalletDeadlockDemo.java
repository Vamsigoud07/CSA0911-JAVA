package task06;

/**
 * Task 06 Part A: Hostel Wallet Transfer - Deadlock Demonstration
 *
 * Demonstrates the classic deadlock scenario when two threads acquire
 * locks in opposite order:
 * Thread 1: Lock(WalletA) -> Lock(WalletB)
 * Thread 2: Lock(WalletB) -> Lock(WalletA)
 *
 * Employs a monitoring daemon / timeout mechanism to detect deadlock
 * and prevent the test suite from hanging indefinitely.
 */
class Wallet {
    private final String id;
    private double balance;

    public Wallet(String id, double balance) {
        this.id = id;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public void withdraw(double amount) {
        this.balance -= amount;
    }

    public void deposit(double amount) {
        this.balance += amount;
    }
}

public class WalletDeadlockDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("   HOSTEL WALLET TRANSFERS: DEADLOCK DEMO         ");
        System.out.println("   Student: K VENKATA VAMSI GOUD (192472021)      ");
        System.out.println("==================================================");

        Wallet walletA = new Wallet("Wallet-A", 1000.0);
        Wallet walletB = new Wallet("Wallet-B", 1000.0);

        // Thread 1: Transfers from A to B
        Thread t1 = new Thread(() -> {
            System.out.println("[Thread-1] Attempting to acquire lock on " + walletA.getId());
            synchronized (walletA) {
                System.out.println("[Thread-1] ACQUIRED lock on " + walletA.getId());
                try {
                    Thread.sleep(50); // Pause to ensure Thread-2 locks Wallet B
                } catch (InterruptedException ignored) {}

                System.out.println("[Thread-1] Waiting to acquire lock on " + walletB.getId() + " (CIRCULAR WAIT)");
                synchronized (walletB) {
                    walletA.withdraw(100.0);
                    walletB.deposit(100.0);
                    System.out.println("[Thread-1] Transfer A -> B successful.");
                }
            }
        }, "Transfer-Thread-1");

        // Thread 2: Transfers from B to A (Reverse lock acquisition order)
        Thread t2 = new Thread(() -> {
            System.out.println("[Thread-2] Attempting to acquire lock on " + walletB.getId());
            synchronized (walletB) {
                System.out.println("[Thread-2] ACQUIRED lock on " + walletB.getId());
                try {
                    Thread.sleep(50); // Pause to ensure Thread-1 locks Wallet A
                } catch (InterruptedException ignored) {}

                System.out.println("[Thread-2] Waiting to acquire lock on " + walletA.getId() + " (CIRCULAR WAIT)");
                synchronized (walletA) {
                    walletB.withdraw(200.0);
                    walletA.deposit(200.0);
                    System.out.println("[Thread-2] Transfer B -> A successful.");
                }
            }
        }, "Transfer-Thread-2");

        // Deadlock Watchdog Monitor Thread
        Thread watchdog = new Thread(() -> {
            try {
                Thread.sleep(300); // 300 ms is more than enough for 50 ms transfers
                if (t1.isAlive() && t2.isAlive()) {
                    System.out.println("\n[DEADLOCK WATCHDOG ALERT] Deadlock Detected!");
                    System.out.println("  Thread-1 State: " + t1.getState() + " (Holding Wallet-A, Waiting for Wallet-B)");
                    System.out.println("  Thread-2 State: " + t2.getState() + " (Holding Wallet-B, Waiting for Wallet-A)");
                    System.out.println("  Coffman Conditions Verified:");
                    System.out.println("    1. Mutual Exclusion: Wallets locked exclusively via synchronized.");
                    System.out.println("    2. Hold and Wait: Thread-1 holds A while requesting B; Thread-2 holds B while requesting A.");
                    System.out.println("    3. No Preemption: JVM cannot forcibly revoke monitor locks.");
                    System.out.println("    4. Circular Wait: Thread-1 -> Wallet-B -> Thread-2 -> Wallet-A -> Thread-1.");
                    System.out.println("\nTerminating demonstration safely via watchdog timeout.");
                    System.exit(0);
                }
            } catch (InterruptedException ignored) {}
        }, "DeadlockWatchdog");

        watchdog.setDaemon(true);
        watchdog.start();

        t1.start();
        t2.start();

        t1.join(1000);
        t2.join(1000);
    }
}
