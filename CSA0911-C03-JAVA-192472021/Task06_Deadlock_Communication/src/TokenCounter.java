package task06;

/**
 * Task 06 Part B - Mandatory "Complete the Code" Program: TokenCounter.java
 *
 * Implements a bounded circular buffer (capacity 5) coordinating between
 * a Mess Token Producer and a Student Consumer.
 *
 * Expected Output:
 * Sum of tokens collected: 210 (expected 210)
 */
public class TokenCounter {
    private final int[] buffer = new int[5];
    private int in = 0;
    private int out = 0;
    private int count = 0;

    // TODO 1: Implement produce method
    public synchronized void produce(int token) throws InterruptedException {
        while (count == buffer.length) {
            wait();
        }
        buffer[in] = token;
        in = (in + 1) % buffer.length;
        count++;
        notifyAll();
    }

    // TODO 2: Implement consume method
    public synchronized int consume() throws InterruptedException {
        while (count == 0) {
            wait();
        }
        int token = buffer[out];
        out = (out + 1) % buffer.length;
        count--;
        notifyAll();
        return token;
    }

    public synchronized int getCount() {
        return count;
    }

    public static void main(String[] args) throws InterruptedException {
        TokenCounter counter = new TokenCounter();
        int totalTokens = 20;
        final int[] collectedSum = {0};

        // Producer Thread: Generates tokens 1 to 20
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= totalTokens; i++) {
                    counter.produce(i);
                    Thread.sleep(5);
                }
            } catch (InterruptedException ignored) {}
        }, "TokenProducer");

        // Consumer Thread: Collects tokens 1 to 20
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= totalTokens; i++) {
                    int token = counter.consume();
                    collectedSum[0] += token;
                    Thread.sleep(8);
                }
            } catch (InterruptedException ignored) {}
        }, "TokenConsumer");

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        int expectedSum = totalTokens * (totalTokens + 1) / 2;
        System.out.println("Sum of tokens collected: " + collectedSum[0] + " (expected " + expectedSum + ")");
    }
}
