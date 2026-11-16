package task06;

/**
 * Task 06 Part B - Experimental Comparison: TokenCounterIfDemo.java
 *
 * Demonstrates the flaw of replacing 'while' with 'if' before wait().
 * When multiple threads wait on the same condition and notifyAll() wakes them,
 * an 'if' check allows a thread to proceed without re-checking the condition!
 * If another thread consumed/produced in the meantime, this leads to
 * buffer overflow (overwriting unread data) or underflow (reading garbage/negative count).
 */
public class TokenCounterIfDemo {
    private final int[] buffer = new int[5];
    private int in = 0;
    private int out = 0;
    private int count = 0;

    // VULNERABLE: Uses 'if' instead of 'while'
    public synchronized void produceWithIf(int token) throws InterruptedException {
        if (count == buffer.length) {
            wait(); // Once awakened, does NOT re-check if buffer is still full!
        }
        buffer[in] = token;
        in = (in + 1) % buffer.length;
        count++;
        if (count > buffer.length) {
            System.err.printf("[ANOMALY DETECTED] Buffer overflow! Count = %d (Max = %d)\n", count, buffer.length);
        }
        notifyAll();
    }

    // VULNERABLE: Uses 'if' instead of 'while'
    public synchronized int consumeWithIf() throws InterruptedException {
        if (count == 0) {
            wait(); // Once awakened, does NOT re-check if buffer is still empty!
        }
        int token = buffer[out];
        out = (out + 1) % buffer.length;
        count--;
        if (count < 0) {
            System.err.printf("[ANOMALY DETECTED] Buffer underflow! Count = %d (Min = 0)\n", count);
        }
        notifyAll();
        return token;
    }

    public synchronized int getCount() {
        return count;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("   TOKEN COUNTER: 'IF' VS 'WHILE' EXPERIMENT      ");
        System.out.println("==================================================");

        TokenCounterIfDemo counter = new TokenCounterIfDemo();
        int tokensPerProducer = 10;

        // Multiple producers and consumers to induce race on wake-up
        Thread p1 = new Thread(() -> produceLoop(counter, 1, tokensPerProducer), "P1");
        Thread p2 = new Thread(() -> produceLoop(counter, 100, tokensPerProducer), "P2");
        Thread c1 = new Thread(() -> consumeLoop(counter, tokensPerProducer), "C1");
        Thread c2 = new Thread(() -> consumeLoop(counter, tokensPerProducer), "C2");

        p1.start(); p2.start();
        c1.start(); c2.start();

        p1.join(); p2.join();
        c1.join(); c2.join();

        System.out.println("\nFinal Buffer Count: " + counter.getCount());
        System.out.println("Rule: Always use 'while (condition) wait();' to protect against spurious wakeups!");
        System.out.println("==================================================");
    }

    private static void produceLoop(TokenCounterIfDemo counter, int start, int count) {
        for (int i = start; i < start + count; i++) {
            try {
                counter.produceWithIf(i);
                Thread.sleep(2);
            } catch (InterruptedException ignored) {}
        }
    }

    private static void consumeLoop(TokenCounterIfDemo counter, int count) {
        for (int i = 0; i < count; i++) {
            try {
                counter.consumeWithIf();
                Thread.sleep(3);
            } catch (InterruptedException ignored) {}
        }
    }
}
