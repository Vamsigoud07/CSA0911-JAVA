package task06;

/**
 * Task 06 Part A: Hostel Wallet Transfer - Deadlock Prevention via Consistent Lock Ordering
 *
 * Implements a strict, global lock acquisition hierarchy based on unique Wallet ID lexicographical order.
 * Both transfer threads always acquire the 'first' lock (lower ID) before the 'second' lock (higher ID),
 * fundamentally breaking the Circular Wait condition and making deadlock mathematically impossible.
 */
public class WalletOrderedLockDemo {

    public static void safeTransfer(Wallet from, Wallet to, double amount) {
        Wallet firstLock;
        Wallet secondLock;

        // Establish strict global ordering
        if (from.getId().compareTo(to.getId()) < 0) {
            firstLock = from;
            secondLock = to;
        } else {
            firstLock = to;
            secondLock = from;
        }

        System.out.printf("[%s] Enforcing lock order: %s first, then %s\n",
                Thread.currentThread().getName(), firstLock.getId(), secondLock.getId());

        synchronized (firstLock) {
            System.out.printf("[%s] Acquired 1st lock: %s\n", Thread.currentThread().getName(), firstLock.getId());
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {}

            synchronized (secondLock) {
                System.out.printf("[%s] Acquired 2nd lock: %s\n", Thread.currentThread().getName(), secondLock.getId());
                if (from.getBalance() >= amount) {
                    from.withdraw(amount);
                    to.deposit(amount);
                    System.out.printf("[%s] Transferred INR %.2f from %s to %s. Success!\n",
                            Thread.currentThread().getName(), amount, from.getId(), to.getId());
                } else {
                    System.out.printf("[%s] Insufficient funds in %s\n", Thread.currentThread().getName(), from.getId());
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("   HOSTEL WALLET: CONSISTENT LOCK ORDERING DEMO   ");
        System.out.println("   Student: K VENKATA VAMSI GOUD (192472021)      ");
        System.out.println("==================================================");

        Wallet walletA = new Wallet("Wallet-A", 1000.0);
        Wallet walletB = new Wallet("Wallet-B", 1000.0);

        Thread t1 = new Thread(() -> safeTransfer(walletA, walletB, 150.0), "Transfer-Thread-1");
        Thread t2 = new Thread(() -> safeTransfer(walletB, walletA, 250.0), "Transfer-Thread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("\n--- Final Balances After Concurrent Transfers ---");
        System.out.printf("%s Balance: INR %.2f\n", walletA.getId(), walletA.getBalance());
        System.out.printf("%s Balance: INR %.2f\n", walletB.getId(), walletB.getBalance());
        System.out.println("Total System Balance: INR " + (walletA.getBalance() + walletB.getBalance()));
        System.out.println("[VERIFIED] All transfers completed with zero deadlock!");
        System.out.println("==================================================");
    }
}
