package task04;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Task 04 - Student B Ten-Run Completion Order Experiment.
 * Runs 10 identical trials with 4 stations under varying thread priorities:
 * Station-1: Priority 1 (MIN_PRIORITY)
 * Station-2: Priority 5 (NORM_PRIORITY)
 * Station-3: Priority 5 (NORM_PRIORITY)
 * Station-4: Priority 10 (MAX_PRIORITY)
 *
 * Records the exact completion sequence of each station in each run to CSV.
 */
public class RunTenTrials {

    public static void main(String[] args) throws IOException, InterruptedException {
        String csvPath = "CSA09-192472021-Assignment3/Task04_Thread_Lifecycle/results/ten_run_results.csv";
        PrintWriter csvWriter = new PrintWriter(new FileWriter(csvPath));
        csvWriter.println("Trial,RunTimeMs,FirstFinished,SecondFinished,ThirdFinished,FourthFinished,DidMaxPriorityWin");

        System.out.println("==================================================");
        System.out.println("   STUDENT B: 10-RUN THREAD SCHEDULING EXPERIMENT ");
        System.out.println("==================================================");

        for (int run = 1; run <= 10; run++) {
            List<String> finishOrder = Collections.synchronizedList(new ArrayList<>());
            long start = System.currentTimeMillis();

            Thread t1 = new Thread(() -> doScan("Station-1", 30, finishOrder), "Station-1");
            Thread t2 = new Thread(() -> doScan("Station-2", 30, finishOrder), "Station-2");
            Thread t3 = new Thread(() -> doScan("Station-3", 30, finishOrder), "Station-3");
            Thread t4 = new Thread(() -> doScan("Station-4", 30, finishOrder), "Station-4");

            t1.setPriority(Thread.MIN_PRIORITY);  // 1
            t2.setPriority(Thread.NORM_PRIORITY); // 5
            t3.setPriority(Thread.NORM_PRIORITY); // 5
            t4.setPriority(Thread.MAX_PRIORITY);  // 10

            t1.start();
            t2.start();
            t3.start();
            t4.start();

            t1.join();
            t2.join();
            t3.join();
            t4.join();

            long elapsed = System.currentTimeMillis() - start;
            boolean maxWon = "Station-4".equals(finishOrder.get(0));

            System.out.printf("Trial #%02d: Duration=%3d ms | Order: %s | MaxPriority(Station-4) 1st? %b\n",
                    run, elapsed, String.join(" -> ", finishOrder), maxWon);

            csvWriter.printf("%d,%d,%s,%s,%s,%s,%b\n",
                    run, elapsed, finishOrder.get(0), finishOrder.get(1), finishOrder.get(2), finishOrder.get(3), maxWon);
        }

        csvWriter.close();
        System.out.println("\nSuccessfully exported all 10 trial measurements to " + csvPath);
    }

    private static void doScan(String name, int count, List<String> finishOrder) {
        for (int i = 0; i < count; i++) {
            // Compute work
            double x = Math.sin(i) * Math.cos(i);
            try {
                Thread.sleep(5);
            } catch (InterruptedException ignored) {}
        }
        finishOrder.add(name);
    }
}
