package task06;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Task 06 Part B - Alternative Concept:
 * Using java.util.concurrent.ArrayBlockingQueue<Integer>
 *
 * Demonstrates:
 * 1. Replacement of low-level wait()/notifyAll() boilerplate with high-level queue primitives:
 *    put() blocks when full, take() blocks when empty.
 * 2. Uses ReentrantLock and two distinct Condition objects (notEmpty, notFull) internally,
 *    eliminating spurious wakeups and thread thrashing.
 */
public class ArrayBlockingQueueDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("   ALTERNATIVE: ArrayBlockingQueue TOKEN COUNTER  ");
        System.out.println("   Student: K VENKATA VAMSI GOUD (192472021)      ");
        System.out.println("==================================================");

        int capacity = 5;
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(capacity);
        int totalTokens = 20;
        final int[] collectedSum = {0};

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= totalTokens; i++) {
                    queue.put(i); // Automatically blocks if queue is full
                    System.out.printf("[PRODUCER] Inserted token #%02d | Queue size: %d\n", i, queue.size());
                    Thread.sleep(5);
                }
            } catch (InterruptedException ignored) {}
        }, "QueueProducer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= totalTokens; i++) {
                    int token = queue.take(); // Automatically blocks if queue is empty
                    collectedSum[0] += token;
                    System.out.printf("  [CONSUMER] Retrieved token #%02d | Cumulative sum: %d\n", token, collectedSum[0]);
                    Thread.sleep(8);
                }
            } catch (InterruptedException ignored) {}
        }, "QueueConsumer");

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        int expectedSum = totalTokens * (totalTokens + 1) / 2;
        System.out.println("\n--- Final Collection Summary ---");
        System.out.println("Sum of tokens collected: " + collectedSum[0] + " (expected " + expectedSum + ")");
        System.out.println("ArrayBlockingQueue handles all locking and condition signaling transparently.");
        System.out.println("==================================================");
    }
}
