package task06;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Task 06 Part A - Alternative Concept:
 * Deadlock Avoidance using ReentrantLock.tryLock() with Timeout.
 *
 * Instead of waiting indefinitely for a lock (Coffman condition 'Hold and Wait'),
 * if a thread cannot acquire the second lock within a specified timeout,
 * it voluntarily releases its first lock, backs off, and retries later.
 */
class LockableWallet {
    private final String id;
    private double balance;
    public final ReentrantLock lock = new ReentrantLock();

    public LockableWallet(String id, double balance) {
        this.id = id;
        this.balance = balance;
    }

    public String getId() { return id; }
    public double getBalance() { return balance; }
    public void withdraw(double amt) { balance -= amt; }
    public void deposit(double amt) { balance += amt; }
}

public class WalletTryLockDemo {

    public static boolean transferWithTryLock(LockableWallet from, LockableWallet to, double amount)
            throws InterruptedException {
        String thread = Thread.currentThread().getName();

        while (true) {
            // Attempt to acquire fromLock
            if (from.lock.tryLock(50, TimeUnit.MILLISECONDS)) {
                try {
                    System.out.printf("[%s] Acquired lock on %s\n", thread, from.getId());
                    Thread.sleep(10); // Hold time

                    // Attempt to acquire toLock
                    if (to.lock.tryLock(50, TimeUnit.MILLISECONDS)) {
                        try {
                            System.out.printf("[%s] Acquired lock on %s. Executing transfer.\n", thread, to.getId());
                            from.withdraw(amount);
                            to.deposit(amount);
                            return true;
                        } finally {
                            to.lock.unlock();
                        }
                    } else {
                        System.out.printf("[%s] Could not acquire lock on %s (Timeout). Releasing %s to avoid deadlock!\n",
                                thread, to.getId(), from.getId());
                    }
                } finally {
                    from.lock.unlock();
                }
            }

            // Backoff pause before retry
            Thread.sleep((int) (Math.random() * 20 + 10));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("   ALTERNATIVE: ReentrantLock.tryLock() BACKOFF   ");
        System.out.println("==================================================");

        LockableWallet wA = new LockableWallet("Wallet-A", 1000.0);
        LockableWallet wB = new LockableWallet("Wallet-B", 1000.0);

        Thread t1 = new Thread(() -> {
            try {
                transferWithTryLock(wA, wB, 100.0);
            } catch (InterruptedException ignored) {}
        }, "Worker-1");

        Thread t2 = new Thread(() -> {
            try {
                transferWithTryLock(wB, wA, 200.0);
            } catch (InterruptedException ignored) {}
        }, "Worker-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("\n--- Final Balances ---");
        System.out.printf("%s: INR %.2f\n", wA.getId(), wA.getBalance());
        System.out.printf("%s: INR %.2f\n", wB.getId(), wB.getBalance());
        System.out.println("Successful completion via lock timeout and backoff.");
        System.out.println("==================================================");
    }
}
